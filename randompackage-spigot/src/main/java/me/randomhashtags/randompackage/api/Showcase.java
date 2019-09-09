package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.util.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class Showcase extends RPFeature implements CommandExecutor {
	private static Showcase instance;
	public static Showcase getShowcase() {
		if(instance == null) instance = new Showcase();
		return instance;
	}

	public YamlConfiguration config;
	
	private ItemStack addItemConfirm, addItemCancel, removeItemConfirm, removeItemCancel, expansion;
	private int addedRows = 0;
	
	private UInventory additems, removeitems;
	private String othertitle, selftitle, TCOLOR;
	private ArrayList<Integer> itemslots;
	
	private ArrayList<Player> inSelf, inOther;
	private HashMap<Player, Integer> deleteSlot;

	public String getIdentifier() { return "SHOWCASE"; }
	protected RPFeature getFeature() { return getShowcase(); }
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
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
					final OfflinePlayer t = Bukkit.getOfflinePlayer(a);
					if(t != null) {
						open(player, t, 1);
					}
				}
			}
		}
		if(l >= 2) {
			final String a = args[0], t = args[1];
			if(a.equals("reset")) {
				resetShowcases(Bukkit.getOfflinePlayer(t));
			}
		}
		return true;
	}

	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "showcase.yml");

		config = YamlConfiguration.loadConfiguration(new File(rpd, "showcase.yml"));
		expansion = d(config, "items.expansion");
		givedpitem.items.put("showcaseexpansion", expansion);
		givedpitem.items.put("showcaseexpander", expansion);
		addedRows = config.getInt("items.expansion.added rows");

		itemslots = new ArrayList<>();
		inSelf = new ArrayList<>();
		inOther = new ArrayList<>();
		deleteSlot = new HashMap<>();

		additems = new UInventory(null, config.getInt("add item.size"), ChatColor.translateAlternateColorCodes('&', config.getString("add item.title")));
		removeitems = new UInventory(null, config.getInt("remove item.size"), ChatColor.translateAlternateColorCodes('&', config.getString("remove item.title")));

		othertitle = ChatColor.translateAlternateColorCodes('&', config.getString("settings.other title"));
		selftitle = ChatColor.translateAlternateColorCodes('&', config.getString("settings.self title"));
		TCOLOR = config.getString("settings.time color");

		addItemConfirm = d(config, "add item.confirm");
		addItemCancel = d(config, "add item.cancel");
		removeItemConfirm = d(config, "remove item.confirm");
		removeItemCancel = d(config, "remove item.cancel");

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
		sendConsoleMessage("&6[RandomPackage] &aLoaded Showcase &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		for(Player p : new ArrayList<>(inSelf)) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!)&r &eYou've been forced to exit a showcase due to reloading the server."));
			p.closeInventory();
		}
		for(Player p : new ArrayList<>(inOther)) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!)&r &eYou've been forced to exit a showcase due to reloading the server."));
			p.closeInventory();
		}
	}

	public void resetShowcases(OfflinePlayer player) {
		if(player != null) {
			final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
			final boolean loaded = pdata.isLoaded;
			if(!loaded) pdata.load();
			pdata.resetShowcases();
			if(!loaded) pdata.unload();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final ItemStack c = event.getCurrentItem();
		if(c != null && !c.getType().equals(Material.AIR)) {
			final Player player = (Player) event.getWhoClicked();
			final Inventory top = player.getOpenInventory().getTopInventory();
			final String t = event.getView().getTitle();
			final boolean edit = t.equals(additems.getTitle()) || t.equals(removeitems.getTitle());
			
			if(edit) {
				final ItemStack item = top.getItem(itemslots.get(0));
				if(t.equals(additems.getTitle()) && c.equals(addItemConfirm)) {
					add(player.getUniqueId(), item, 1);
				} else if(c.equals(removeItemConfirm)) {
					delete(player.getUniqueId(), 1, top.getItem(deleteSlot.get(player)));
					deleteSlot.remove(player);
				}
			} else if(inSelf.contains(player)) {
				if(event.getRawSlot() >= top.getSize()) {
					confirmAddition(player, c);
				} else {
					confirmDeletion(player, c);
					deleteSlot.put(player, itemslots.get(1));
				}
			} else return;
			
			event.setCancelled(true);
			player.updateInventory();
			if(edit && (c.equals(addItemConfirm) || c.equals(addItemCancel) || c.equals(removeItemConfirm) || c.equals(removeItemCancel))) {
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
			final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
			final HashMap<Integer, Integer> sizes = pdata.getShowcaseSizes();
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
	

	public void confirmAddition(Player player, ItemStack item) {
		if(hasPermission(player, "RandomPackage.showcase.add", true)) {
			confirm(player, item, additems);
		}
	}
	public void confirmDeletion(Player player, ItemStack item) {
		if(hasPermission(player, "RandomPackage.showcase.remove", true)) {
			confirm(player, item, removeitems);
		}
	}
	private void confirm(Player player, ItemStack item, UInventory type) {
		if(item != null && !item.getType().equals(Material.AIR)) {
			player.openInventory(Bukkit.createInventory(player, type.getSize(), type.getTitle()));
			final Inventory top = player.getOpenInventory().getTopInventory();
			top.setContents(type.getInventory().getContents());
			for(int i : itemslots) top.setItem(i, item);
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
			itemMeta = item.getItemMeta(); lore.clear();
			if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
			lore.add(ChatColor.translateAlternateColorCodes('&', TCOLOR + format));
			itemMeta.setLore(lore); lore.clear();
			item.setItemMeta(itemMeta);

			final RPPlayer pdata = RPPlayer.get(player);
			pdata.addToShowcase(page, item);
		}
	}
	private void delete(UUID player, int page, ItemStack is) {
		final RPPlayer pdata = RPPlayer.get(player);
		pdata.removeFromShowcase(page, is);
	}
	public void open(Player opener, OfflinePlayer target, int page) {
		if(target == null || target == opener) target = opener;
		final boolean self = target == opener;
		final RPPlayer pdata = RPPlayer.get(target.getUniqueId());
		final HashMap<Integer, ItemStack[]> showcases = pdata.getShowcases();
		int maxpage = 0;
		for(int i = 1; i <= 100; i++) {
			if(showcases.containsKey(i)) maxpage = i;
		}
		if(!hasPermission(opener, "RandomPackage.showcase" + (self ? "" : ".other"), false)) {
			sendStringListMessage(opener, config.getStringList("messages.no access"), null);
		} else {
			int size = pdata.getShowcaseSize(page);
			size = size == 0 ? 9 : size;
			(self ? inSelf : inOther).add(opener);
			final Inventory inv = Bukkit.createInventory(opener, size, (self ? selftitle : othertitle).replace("{PLAYER}", (self ? opener : target).getName()).replace("{PAGE}", Integer.toString(page)).replace("{MAX}", Integer.toString(maxpage)));
			opener.openInventory(inv);
			final Inventory top = opener.getOpenInventory().getTopInventory();
			final HashMap<Integer, ItemStack> showcase = pdata.getShowcaseItems(page);
			for(int i : showcase.keySet()) if(i < size) top.setItem(i, showcase.get(i));
			opener.updateInventory();
		}
	}
}
