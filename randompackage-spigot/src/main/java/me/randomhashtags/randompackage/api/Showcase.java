package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.ShowcaseData;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public enum Showcase implements RPFeatureSpigot, CommandExecutor {
	INSTANCE;

	public YamlConfiguration config;
	private ItemStack addItemConfirm, addItemCancel, removeItemConfirm, removeItemCancel, expansion;
	private int addedRows = 0;
	private UInventory additems, removeitems;
	private String othertitle, selftitle, TCOLOR;
	private List<Integer> itemslots;
	private List<Player> inSelf, inOther;
	private HashMap<Player, Integer> deleteSlot;

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		final int l = args.length;
		if(player != null) {
			if(l == 0) {
				open(player, player, 1);
			} else {
				final String a = args[0];
				if(a.startsWith("add")) {
					confirmAddition(player, player.getItemInHand());
				} else if(hasPermission(player, "RandomPackage.showcase.other", true)) {
					final OfflinePlayer target = Bukkit.getOfflinePlayer(a);
					open(player, target, 1);
				}
			}
		}
		if(l >= 2) {
			final String a = args[0];
			if(a.equals("reset")) {
				resetShowcases(Bukkit.getOfflinePlayer(args[1]));
			}
		}
		return true;
	}

	@Override
	public void load() {
		save(null, "showcase.yml");

		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "showcase.yml"));
		expansion = createItemStack(config, "items.expansion");
		GivedpItem.INSTANCE.items.put("showcaseexpansion", expansion);
		GivedpItem.INSTANCE.items.put("showcaseexpander", expansion);
		addedRows = config.getInt("items.expansion.added rows");

		itemslots = new ArrayList<>();
		inSelf = new ArrayList<>();
		inOther = new ArrayList<>();
		deleteSlot = new HashMap<>();

		additems = new UInventory(null, config.getInt("add item.size"), colorize(config.getString("add item.title")));
		removeitems = new UInventory(null, config.getInt("remove item.size"), colorize(config.getString("remove item.title")));

		othertitle = colorize(config.getString("settings.other title"));
		selftitle = colorize(config.getString("settings.self title"));
		TCOLOR = config.getString("settings.time color");

		addItemConfirm = createItemStack(config, "add item.confirm");
		addItemCancel = createItemStack(config, "add item.cancel");
		removeItemConfirm = createItemStack(config, "remove item.confirm");
		removeItemCancel = createItemStack(config, "remove item.cancel");

		final Inventory ai = additems.getInventory(), ri = removeitems.getInventory();
		for(int i = 1; i <= 2; i++) {
			for(int o = 0; o < (i == 1 ? additems.getSize() : removeitems.getSize()); o++) {
				String s = config.getString((i == 1 ? "add item." : "remove item.") + o + ".item");
				if(s != null) {
					if(s.equals("{CONFIRM}")) {
						if(i == 1) ai.setItem(o, addItemConfirm.clone());
						else       ri.setItem(o, removeItemConfirm.clone());
					} else if(s.equals("{CANCEL}")) {
						(i == 1 ? ai : ri).setItem(o, i == 1 ? addItemCancel : removeItemCancel);
					} else if(s.equals("{ITEM}")) itemslots.add(o);
				}
			}
		}
	}
	@Override
	public void unload() {
		final String msg = colorize("&e&l(!)&r &eYou've been forced to exit a showcase due to reloading the server.");
		for(Player player : new ArrayList<>(inSelf)) {
			player.sendMessage(msg);
			player.closeInventory();
		}
		for(Player player : new ArrayList<>(inOther)) {
			player.sendMessage(msg);
			player.closeInventory();
		}
	}

	public void resetShowcases(OfflinePlayer player) {
		if(player != null) {
			final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
			pdata.getShowcaseData().reset();
		}
	}
	public void confirmAddition(@NotNull Player player, @NotNull ItemStack item) {
		if(hasPermission(player, "RandomPackage.showcase.add", true)) {
			confirm(player, item, additems);
		}
	}
	public void confirmDeletion(@NotNull Player player, @NotNull ItemStack item) {
		if(hasPermission(player, "RandomPackage.showcase.remove", true)) {
			confirm(player, item, removeitems);
		}
	}
	private void confirm(Player player, ItemStack item, UInventory type) {
		if(item != null && !item.getType().equals(Material.AIR)) {
			player.openInventory(Bukkit.createInventory(player, type.getSize(), type.getTitle()));
			final Inventory top = player.getOpenInventory().getTopInventory();
			top.setContents(type.getInventory().getContents());
			for(int i : itemslots) {
				top.setItem(i, item);
			}
		}
		player.updateInventory();
	}
	private void add(UUID player, ItemStack item, int page) {
		if(item == null || item.getType().equals(Material.AIR)) {
		} else {
		    final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
			if(op.isOnline()) {
				removeItem(op.getPlayer(), item, item.getAmount());
			}
			final String format = toReadableDate(new Date(), "MMMM dd, yyyy");
			final ItemMeta itemMeta = item.getItemMeta();
			final List<String> lore = new ArrayList<>();
			if(itemMeta.hasLore()) {
				lore.addAll(itemMeta.getLore());
			}
			lore.add(colorize(TCOLOR + format));
			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);

			final FileRPPlayer pdata = FileRPPlayer.get(player);
			pdata.getShowcaseData().addToShowcase(page, item);
		}
	}
	private void delete(UUID player, int page, ItemStack is) {
		final FileRPPlayer pdata = FileRPPlayer.get(player);
		pdata.getShowcaseData().removeFromShowcase(page, is);
	}
	public void open(@NotNull Player opener, @Nullable OfflinePlayer target, int page) {
		if(target == null || target == opener) target = opener;
		final boolean self = target == opener;
		final FileRPPlayer pdata = FileRPPlayer.get(target.getUniqueId());
		final ShowcaseData data = pdata.getShowcaseData();
		final HashMap<Integer, ItemStack[]> showcases = data.getShowcases();
		int maxpage = 0;
		for(int i = 1; i <= 100; i++) {
			if(showcases.containsKey(i)) {
				maxpage = i;
			}
		}
		if(!hasPermission(opener, "RandomPackage.showcase" + (self ? "" : ".other"), false)) {
			sendStringListMessage(opener, getStringList(config, "messages.no access"), null);
		} else {
			int size = data.getSize(page);
			size = size == 0 ? 9 : size;
			(self ? inSelf : inOther).add(opener);
			final Inventory inv = Bukkit.createInventory(opener, size, (self ? selftitle : othertitle).replace("{PLAYER}", (self ? opener : target).getName()).replace("{PAGE}", Integer.toString(page)).replace("{MAX}", Integer.toString(maxpage)));
			opener.openInventory(inv);
			final Inventory top = opener.getOpenInventory().getTopInventory();
			final ItemStack[] showcase = pdata.getShowcaseData().getShowcaseItems(page);
			int slot = 0;
			for(ItemStack is : showcase) {
				if(slot < size) {
					top.setItem(slot, is);
				}
				slot += 1;
			}
			opener.updateInventory();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final ItemStack current = event.getCurrentItem();
		if(current != null && !current.getType().equals(Material.AIR)) {
			final Player player = (Player) event.getWhoClicked();
			final Inventory top = player.getOpenInventory().getTopInventory();
			final String title = event.getView().getTitle();
			final boolean isAdding = title.equals(additems.getTitle()), edit = isAdding || title.equals(removeitems.getTitle());

			if(edit) {
				final ItemStack item = top.getItem(itemslots.get(0));
				if(isAdding && current.equals(addItemConfirm)) {
					add(player.getUniqueId(), item, 1);
				} else if(current.equals(removeItemConfirm)) {
					delete(player.getUniqueId(), 1, top.getItem(deleteSlot.get(player)));
					deleteSlot.remove(player);
				}
			} else if(inSelf.contains(player)) {
				if(event.getRawSlot() >= top.getSize()) {
					confirmAddition(player, current);
				} else {
					confirmDeletion(player, current);
					deleteSlot.put(player, itemslots.get(1));
				}
			} else {
				return;
			}

			event.setCancelled(true);
			player.updateInventory();
			if(edit && (current.equals(addItemConfirm) || current.equals(addItemCancel) || current.equals(removeItemConfirm) || current.equals(removeItemCancel))) {
				open(player, player, 1);
				inSelf.add(player);
			}
		}
	}
	@EventHandler
	private void inventoryCloseEvent(InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();
		inSelf.remove(player);
		inOther.remove(player);
		deleteSlot.remove(player);
	}
	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack i = event.getItem();
		if(i != null && i.isSimilar(expansion)) {
			final Player player = event.getPlayer();
			final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
			final HashMap<Integer, Integer> sizes = pdata.getShowcaseData().getSizes();
			event.setCancelled(true);
			player.updateInventory();
			for(int o = 1; o <= 10; o++) {
				if(sizes.containsKey(o)) {
					final int size = sizes.get(o);
					if(size != 54) {
						sizes.put(o, size + (addedRows * 9));
						removeItem(player, i, 1);
						return;
					}
				}
			}
		}
	}
}
