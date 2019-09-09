package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.event.regional.FactionLeaveEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.addon.obj.Home;
import me.randomhashtags.randompackage.util.universal.UInventory;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.*;
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
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class Homes extends RPFeature implements CommandExecutor {
	private static Homes instance;
	public static Homes getHomes() {
		if(instance == null) instance = new Homes();
		return instance;
	}

	public YamlConfiguration config;
	public int defaultMax;
	public ItemStack maxHomeIncreaser;

	private List<Player> viewingHomes;
	private HashMap<Player, Home> editingIcons;
	private UInventory editicon;

	public String getIdentifier() { return "HOMES"; }
	protected RPFeature getFeature() { return getHomes(); }
	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "homes.yml");

		config = YamlConfiguration.loadConfiguration(new File(rpd, "homes.yml"));
		defaultMax = config.getInt("settings.default max");
		maxHomeIncreaser = d(config, "items.max home increaser");
		givedpitem.items.put("maxhomeincrease", maxHomeIncreaser);

		viewingHomes = new ArrayList<>();
		editingIcons = new HashMap<>();

		editicon = new UInventory(null, config.getInt("edit icon.size"), ChatColor.translateAlternateColorCodes('&', config.getString("edit icon.title")));
		final Inventory eii = editicon.getInventory();
		final List<String> addedlore = colorizeListString(config.getStringList("edit icon.added lore"));
		for(String s : config.getConfigurationSection("edit icon").getKeys(false)) {
			if(!s.equals("title") && !s.equals("size") && !s.equals("added lore")) {
				item = d(config, "edit icon." + s); itemMeta = item.getItemMeta(); lore.clear();
				if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
				lore.addAll(addedlore);
				itemMeta.setLore(lore); lore.clear();
				item.setItemMeta(itemMeta);
				eii.setItem(config.getInt("edit icon." + s + ".slot"), item);
			}
		}
		sendConsoleMessage("&6[RandomPackage] &aLoaded Homes &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		givedpitem.items.remove("maxhomeincreaser");
		for(Player p : viewingHomes) p.closeInventory();
		for(Player player : editingIcons.keySet()) player.closeInventory();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!(sender instanceof Player)) return true;
		final Player player = (Player) sender;
		final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
		final String c = cmd.getName();
		final boolean zero = args.length == 0;
		if(c.equals("home")) {
			if(zero) {
				viewHomes(player, pdata);
			} else {
				final Home home = pdata.getHome(getArguments(args));
				if(home != null)
					teleportToHome(player, home);
				else
					viewHomes(player, pdata);
			}
		} else if(c.equals("sethome")) {
			if(hasPermission(player, "RandomPackage.sethome", true) && pdata.getHomes().size()+1 <= pdata.getMaxHomes()) {
				final String z = zero ? config.getString("settings.default name") : getArguments(args);
				pdata.addHome(player.getLocation(), z, UMaterial.GRASS_BLOCK);
				final HashMap<String, String> replacements = new HashMap<>();
				replacements.put("{HOME}", z);
				sendStringListMessage(player, config.getStringList("messages.set home"), replacements);
			}
		}
		return true;
	}
	
	private String getArguments(String[] args) {
		final StringBuilder s = new StringBuilder();
		final int l = args.length;
		for(int i = 0; i < l; i++) s.append(args[i]).append(i == l-1 ? "" : " ");
		return s.toString();
	}
	public void viewHomes(Player opener, RPPlayer target) {
		if(hasPermission(opener, "RandomPackage.home", true)) {
			viewingHomes.add(opener);
			final List<Home> homes = target.getHomes();
			final String name = ChatColor.translateAlternateColorCodes('&', config.getString("menu.name"));
			final List<String> l = config.getStringList("menu.lore");
			opener.openInventory(Bukkit.createInventory(opener, ((homes.size() + 9) / 9) * 9, ChatColor.translateAlternateColorCodes('&', config.getString("menu.title").replace("{SET}", Integer.toString(homes.size())).replace("{MAX}", Integer.toString(target.getMaxHomes())))));
			final Inventory top = opener.getOpenInventory().getTopInventory();
			for(Home h : homes) {
				final Location lo = h.location;
				final String w = lo.getWorld().getName();
				final double x = round(lo.getX(), 1), y = round(lo.getY(), 1), z = round(lo.getZ(), 1);
				item = h.icon.getItemStack(); itemMeta = item.getItemMeta(); lore.clear();
				itemMeta.setDisplayName(name.replace("{HOME}", h.name));
				for(String s : l) lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("{WORLD}", w).replace("{X}", Double.toString(x)).replace("{Y}", Double.toString(y)).replace("{Z}", Double.toString(z))));
				itemMeta.setLore(lore); lore.clear();
				item.setItemMeta(itemMeta);
				top.setItem(top.firstEmpty(), item);
			}
			opener.updateInventory();
		}
	}
	public void teleportToHome(Player player, Home home) {
		if(hasPermission(player, "RandomPackage.home.teleport", true))
			player.teleport(home.location, TeleportCause.PLUGIN);
	}
	public void editIcon(Player player, Home home) {
		player.closeInventory();
		player.openInventory(Bukkit.createInventory(player, editicon.getSize(), editicon.getTitle()));
		player.getOpenInventory().getTopInventory().setContents(editicon.getInventory().getContents());
		player.updateInventory();
		editingIcons.put(player, home);
	}
	@EventHandler
	private void inventoryCloseEvent(InventoryCloseEvent event) {
		final Player p = (Player) event.getPlayer();
		viewingHomes.remove(p);
		editingIcons.remove(p);
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final boolean v = viewingHomes.contains(player);
		if(v || editingIcons.containsKey(player)) {
			event.setCancelled(true);
			player.updateInventory();
			final int r = event.getRawSlot();
			final ItemStack c = event.getCurrentItem();
			final String click = event.getClick().name();
			final Inventory top = player.getOpenInventory().getTopInventory();
			if(r < 0 || r >= top.getSize() || !click.contains("RIGHT") && !click.contains("LEFT") && !click.contains("MIDDLE") || c == null || c.getType().equals(Material.AIR)) return;
			if(v) {
				final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
				final List<Home> homes = pdata.getHomes();
				player.closeInventory();
				final Home h = homes.get(r);
				if(click.contains("LEFT")) {
					teleportToHome(player, h);
				} else if(click.equals("MIDDLE")) {
					final HashMap<String, String> replacements = new HashMap<>();
					replacements.put("{HOME}", h.name);
					sendStringListMessage(player, config.getStringList("messages.delete"), replacements);
					pdata.deleteHome(h);
				} else if(click.contains("RIGHT")) {
					editIcon(player, h);
				}
			} else {
				final Home h = editingIcons.get(player);
				final UMaterial um = UMaterial.match(c);
				final String n = h.name, umn = um.name();
				h.icon = um;
				player.closeInventory();
				final HashMap<String, String> replacements = new HashMap<>();
				replacements.put("{HOME}", n);
				replacements.put("{ICON}", umn);
				sendStringListMessage(player, config.getStringList("messages.save icon"), replacements);
			}
			player.updateInventory();
		}
	}
	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack is = event.getItem();
		if(is != null && is.isSimilar(maxHomeIncreaser)) {
			final Player player = event.getPlayer();
			event.setCancelled(true);
			removeItem(player, is, 1);
			player.updateInventory();
			RPPlayer.get(player.getUniqueId()).addedMaxHomes += 1;
			sendStringListMessage(player, config.getStringList("messages.unlocked new home slot"), null);
		}
	}
	@EventHandler
	private void factionLeaveEvent(FactionLeaveEvent event) {
		final Player player = event.player;
		final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
		final List<Home> homes = pdata.getHomes();
		if(homes != null && !homes.isEmpty()) {
			final List<String> msg = config.getStringList("messages.deleted due to inside a faction claim");
			final HashMap<String, String> replacements = new HashMap<>();
			final List<Chunk> c = factions.getRegionalChunks(event.faction);
			for(Home h : new ArrayList<>(homes)) {
				final Location l = h.location;
				if(c.contains(l.getChunk())) {
					replacements.put("{HOME}", h.name);
					replacements.put("{X}", formatInt(l.getBlockX()));
					replacements.put("{Y}", formatInt(l.getBlockY()));
					replacements.put("{Z}", formatInt(l.getBlockZ()));
					sendStringListMessage(player, msg, replacements);
					homes.remove(h);
				}
			}
		}
	}
}
