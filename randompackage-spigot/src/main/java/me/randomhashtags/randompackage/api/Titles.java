package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.Title;
import me.randomhashtags.randompackage.addon.file.FileTitle;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Titles extends RPFeature implements CommandExecutor {
	private static Titles instance;
	public static Titles getTitles() {
		if(instance == null) instance = new Titles();
		return instance;
	}

	public YamlConfiguration config;
	public ItemStack interactableItem;
	private ItemStack nextpage, background, active, inactive;
	private String selftitle, chatformat, tabformat;
	private HashMap<Player, Integer> pages;

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		if(player != null) {
			final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
			if(args.length == 0) {
				viewTitles(player, pdata, 1);
			}
		}
		return true;
	}

	public String getIdentifier() {
		return "TITLES";
	}
	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "titles.yml");

		pages = new HashMap<>();

		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "titles.yml"));
		interactableItem = d(config, "interactable item");
		FileTitle.i = interactableItem;
		nextpage = d(config, "gui.next page");
		background = d(config, "gui.background");
		active = d(config, "gui.active title");
		inactive = d(config, "gui.inactive title");
		for(String s : config.getStringList("titles")) {
			new FileTitle(s);
		}
		selftitle = colorize(config.getString("gui.self title"));
		//othertitle = colorize(config.getString("gui.other-title"));
		chatformat = colorize(config.getString("chat.format"));
		tabformat = colorize(config.getString("tab.format"));
		FileTitle.titleChatFormat = chatformat;
		FileTitle.titleTabFormat = tabformat;
		sendConsoleDidLoadFeature(getAll(Feature.TITLE).size() + " Titles", started);
	}
	public void unload() {
		for(Player player : new ArrayList<>(pages.keySet())) {
			player.closeInventory();
		}
		unregister(Feature.TITLE);
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack i = event.getItem();
		if(i != null && i.hasItemMeta() && i.getType().equals(interactableItem.getType())) {
			final Title title = valueOfTitle(i);
			if(title != null) {
				final Player player = event.getPlayer();
				event.setCancelled(true);
				player.updateInventory();
				final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
				final boolean has = pdata.getTitles().contains(title);
				for(String s : getStringList(config, "messages." + (has ? "already own" : "redeem"))) {
					s = s.replace("{TITLE}", colorize(title.getIdentifier()));
					player.sendMessage(s);
				}
				if(!has) {
					pdata.addTitle(title);
					removeItem(player, i, 1);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final Inventory top = player.getOpenInventory().getTopInventory();
		final ItemStack current = event.getCurrentItem();
		if(top.getHolder() == player && current != null && event.getView().getTitle().equals(selftitle)) {
			final int slot = event.getRawSlot();
			event.setCancelled(true);
			player.updateInventory();
			if(slot >= top.getSize()) {
				return;
			}
			final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
			final Title title = pdata.getActiveTitle();
			final String activeTitle = title != null ? title.getIdentifier() : null;
			final List<Title> titles = pdata.getTitles();
			final int page = pages.getOrDefault(player, 0), Z = (page*53)+slot;
			if(current.equals(nextpage)) {
				player.closeInventory();
				viewTitles(player, pdata, page+2);
			} else if(titles.size() > Z) {
				final Title target = titles.get(Z);
				final String id = target.getIdentifier(), coloredId = colorize(id);
				final String type = (activeTitle != null && activeTitle.equals(id) ? "un" : "") + "equip";
				for(String s : getStringList(config, "messages." + type)) {
					player.sendMessage(s.replace("{TITLE}", coloredId));
				}
				pdata.setActiveTitle(type.equals("unequip") ? null : target);
				update(player, pdata);
			}
		}
	}
	@EventHandler
	private void inventoryCloseEvent(InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();
		pages.remove(player);
	}
	private void viewTitles(Player player, RPPlayer pdata, int page) {
		if(hasPermission(player, "RandomPackage.titles", true)) {
			final List<Title> owned = pdata.getTitles();
			page = page-1;
			int size = owned.size()-(53*page);
			if(size <= 0) {
				sendStringListMessage(player, getStringList(config, "messages.no unlocked titles"), null);
			} else {
				final Title activeTitle = pdata.getActiveTitle();
				final String activetitle = activeTitle != null ? activeTitle.getIdentifier() : null;
				pages.put(player, page);
				size = Math.min(size%9 == 0 ? size : ((size+9)/9)*9, 54);
				player.openInventory(Bukkit.createInventory(player, size, selftitle));
				final Inventory top = player.getOpenInventory().getTopInventory();
				for(Title title : owned) {
					final int f = top.firstEmpty();
					if(f > -1) {
						top.setItem(f, getTitle(activetitle, title));
					} else {
						break;
					}
				}
				for(int p = 0; p < top.getSize(); p++) {
					final ItemStack is = top.getItem(p);
					if(is == null || is.getType().equals(Material.AIR)) {
						top.setItem(p, background);
					}
				}
				player.updateInventory();
			}
		}
	}
	private void update(Player player, RPPlayer pdata) {
		final int page = pages.get(player)-1;
		final List<Title> owned = pdata.getTitles();
		final Title A = pdata.getActiveTitle();
		final String activetitle = A != null ? A.getIdentifier() : null;
		final Inventory top = player.getOpenInventory().getTopInventory();
		for(int i = 0; i < top.getSize() && i < owned.size(); i++) {
			final ItemStack is = top.getItem(i);
			if(is != null && !is.equals(background)) {
				top.setItem(i, getTitle(activetitle, owned.get(i)));
			}
		}
		player.updateInventory();
	}
	private ItemStack getTitle(String activetitle, Title input) {
		final String title = input.getIdentifier();
		item = (title.equals(activetitle) ? active : inactive).clone();
		itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TITLE}", title));
		item.setItemMeta(itemMeta);
		return item;
	}
}
