package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.classes.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Titles extends RandomPackageAPI {

	private static Titles instance;
	public static final Titles getTitles() {
		if(instance == null) instance = new Titles();
		return instance;
	}

	public boolean isEnabled = false;
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

	public void enable() {
		final long started = System.currentTimeMillis();
		if(isEnabled) return;
		pluginmanager.registerEvents(this, randompackage);
		save(null, "titles.yml");
		isEnabled = true;

		pages = new HashMap<>();

		config = YamlConfiguration.loadConfiguration(new File(rpd, "titles.yml"));
		interactableItem = d(config, "interactable item");
		Title.i = interactableItem;
		nextpage = d(config, "gui.next page");
		background = d(config, "gui.background");
		active = d(config, "gui.active title");
		inactive = d(config, "gui.inactive title");
		for(String s : config.getStringList("titles")) {
			new Title(s);
		}
		selftitle = ChatColor.translateAlternateColorCodes('&', config.getString("gui.self title"));
		//othertitle = ChatColor.translateAlternateColorCodes('&', config.getString("gui.other-title"));
		chatformat = ChatColor.translateAlternateColorCodes('&', config.getString("chat.format"));
		tabformat = ChatColor.translateAlternateColorCodes('&', config.getString("tab.format"));
		Title.titleChatFormat = chatformat;
		Title.titleTabFormat = tabformat;
		final HashMap<String, Title> T = Title.titles;
 		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (T != null ? T.size() : 0) + " titles &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void disable() {
		if(!isEnabled) return;
		isEnabled = false;

		config = null;
		interactableItem = null;
		nextpage = null;
		background = null;
		active = null;
		inactive = null;
		selftitle = null;
		tabformat = null;
		for(Player p : pages.keySet()) p.closeInventory();
		pages = null;
		Title.deleteAll();
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack i = event.getItem();
		if(i != null && i.hasItemMeta() && i.getType().equals(interactableItem.getType())) {
			final Title T = Title.valueOf(i);
			if(T != null) {
				final Player player = event.getPlayer();
				event.setCancelled(true);
				player.updateInventory();
				final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
				final boolean has = pdata.getTitles().contains(T);
				final List<String> m = config.getStringList("messages." + (has ? "already own" : "redeem"));
				for(String s : m) {
					s = s.replace("{TITLE}", ChatColor.translateAlternateColorCodes('&', T.getTitle()));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
				}
				if(!has) {
					pdata.addTitle(T);
					removeItem(player, i, 1);
				}
			}
		}
	}
	@EventHandler
	private void inventoryClickEvent(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final Inventory top = player.getOpenInventory().getTopInventory();
		final ItemStack c = event.getCurrentItem();
		if(top.getHolder() == player && !event.isCancelled() && c != null && event.getView().getTitle().equals(selftitle)) {
			final int r = event.getRawSlot();
			event.setCancelled(true);
			player.updateInventory();
			if(r >= top.getSize()) return;
			final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
			final Title T = pdata.getActiveTitle();
			final String a = T != null ? T.getTitle() : null;
			final List<Title> titles = pdata.getTitles();
			final int page = pages.getOrDefault(player, 0), Z = (page*53)+r;
			if(c.equals(nextpage)) {
				player.closeInventory();
				viewTitles(player, pdata, page+2);
			} else if(titles.size() > Z) {
				final String q = (a != null && a.equals(titles.get(Z).getTitle()) ? "un" : "") + "equip";
				final List<String> s = config.getStringList("messages." + q);
				for(String h : s) player.sendMessage(ChatColor.translateAlternateColorCodes('&', h.replace("{TITLE}", titles.get(Z).getTitle())));
				pdata.setActiveTitle(q.equals("unequip") ? null : titles.get(Z));
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
			final List<Title> titles = pdata.getTitles();
			page = page-1;
			int size = titles.size()-(53*page);
			if(size <= 0) {
				sendStringListMessage(player, config.getStringList("messages.no unlocked titles"), null);
			} else {
				final List<Title> owned = pdata.getTitles();
				final Title A = pdata.getActiveTitle();
				final String activetitle = A != null ? A.getTitle() : null;
				pages.put(player, page);
				size = size == 9 || size == 18 || size == 27 || size == 36 || size == 45 || size == 54 ? size : ((size+9)/9)*9;
				size = size > 54 ? 54 : size;
				player.openInventory(Bukkit.createInventory(player, size, selftitle));
				final Inventory top = player.getOpenInventory().getTopInventory();
				for(Title t : owned) {
					top.setItem(top.firstEmpty(), getTitle(activetitle, t));
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
		final String activetitle = A != null ? A.getTitle() : null;
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
		final String title = input.getTitle();
		item = title.equals(activetitle) ? active.clone() : inactive.clone();
		itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TITLE}", title));
		item.setItemMeta(itemMeta);
		return item;
	}
}
