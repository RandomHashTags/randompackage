package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.obj.Home;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.HomeData;
import me.randomhashtags.randompackage.data.RPPlayer;
import me.randomhashtags.randompackage.event.regional.FactionLeaveEvent;
import me.randomhashtags.randompackage.perms.HomePermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
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
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.GIVEDP_ITEM;

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

	public String getIdentifier() {
		return "HOMES";
	}
	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "homes.yml");

		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "homes.yml"));
		defaultMax = config.getInt("settings.default max");
		maxHomeIncreaser = createItemStack(config, "items.max home increaser");
		GIVEDP_ITEM.items.put("maxhomeincrease", maxHomeIncreaser);

		viewingHomes = new ArrayList<>();
		editingIcons = new HashMap<>();

		editicon = new UInventory(null, config.getInt("edit icon.size"), colorize(config.getString("edit icon.title")));
		final Inventory eii = editicon.getInventory();
		final List<String> addedLore = colorizeListString(config.getStringList("edit icon.added lore"));
		for(String s : getConfigurationSectionKeys(config, "edit icon", false)) {
			if(!s.equals("title") && !s.equals("size") && !s.equals("added lore")) {
				item = createItemStack(config, "edit icon." + s);
				itemMeta = item.getItemMeta(); lore.clear();
				if(itemMeta.hasLore()) {
					lore.addAll(itemMeta.getLore());
				}
				lore.addAll(addedLore);
				itemMeta.setLore(lore); lore.clear();
				item.setItemMeta(itemMeta);
				eii.setItem(config.getInt("edit icon." + s + ".slot"), item);
			}
		}
		sendConsoleDidLoadFeature("Homes", started);
	}
	public void unload() {
		GIVEDP_ITEM.items.remove("maxhomeincreaser");
		for(Player player : new ArrayList<>(viewingHomes)) {
			player.closeInventory();
		}
		for(Player player : new ArrayList<>(editingIcons.keySet())) {
			player.closeInventory();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!(sender instanceof Player)) {
			return true;
		}
		final Player player = (Player) sender;
		final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
		final HomeData data = pdata.getHomeData();
		final String c = cmd.getName();
		final boolean zero = args.length == 0;
		if(c.equals("home")) {
			if(zero) {
				viewHomes(player, pdata);
			} else {
				final Home home = data.getHome(getArguments(args));
				if(home != null) {
					teleportToHome(player, home);
				} else {
					viewHomes(player, pdata);
				}
			}
		} else if(c.equals("sethome")) {
			if(hasPermission(player, HomePermission.COMMAND_SETHOME, true) && data.getHomes().size()+1 <= data.getMaxHomes(player)) {
				final String z = zero ? config.getString("settings.default name") : getArguments(args);
				data.addHome(player.getLocation(), z, UMaterial.GRASS_BLOCK);
				final HashMap<String, String> replacements = new HashMap<>();
				replacements.put("{HOME}", z);
				sendStringListMessage(player, getStringList(config, "messages.set home"), replacements);
			}
		}
		return true;
	}
	
	private String getArguments(String[] args) {
		final StringBuilder s = new StringBuilder();
		final int l = args.length;
		for(int i = 0; i < l; i++) {
			s.append(args[i]).append(i == l-1 ? "" : " ");
		}
		return s.toString();
	}
	public void viewHomes(@NotNull Player opener, @NotNull RPPlayer target) {
		if(hasPermission(opener, HomePermission.VIEW_HOMES, true)) {
			viewingHomes.add(opener);
			final HomeData data = target.getHomeData();
			final List<Home> homes = data.getHomes();
			final String name = getString(config, "menu.name");
			final List<String> menuLore = getStringList(config, "menu.lore");
			opener.openInventory(Bukkit.createInventory(opener, ((homes.size() + 9) / 9) * 9, colorize(config.getString("menu.title").replace("{SET}", Integer.toString(homes.size())).replace("{MAX}", Integer.toString(data.getMaxHomes(opener))))));
			final Inventory top = opener.getOpenInventory().getTopInventory();
			for(Home home : homes) {
				final Location loc = home.getLocation();
				final String world = loc.getWorld().getName(), x = Double.toString(round(loc.getX(), 1)), y = Double.toString(round(loc.getY(), 1)), z = Double.toString(round(loc.getZ(), 1));
				item = home.getIcon().getItemStack();
				itemMeta = item.getItemMeta(); lore.clear();
				itemMeta.setDisplayName(name.replace("{HOME}", home.getName()));
				for(String s : menuLore) {
					lore.add(s.replace("{WORLD}", world).replace("{X}", x).replace("{Y}", y).replace("{Z}", z));
				}
				itemMeta.setLore(lore); lore.clear();
				item.setItemMeta(itemMeta);
				top.setItem(top.firstEmpty(), item);
			}
			opener.updateInventory();
		}
	}
	public void teleportToHome(@NotNull Player player, @NotNull Home home) {
		if(hasPermission(player, HomePermission.TELEPORT_TO_HOME, true)) {
			player.teleport(home.getLocation(), TeleportCause.PLUGIN);
		}
	}
	public void editIcon(@NotNull Player player, @NotNull Home home) {
		player.closeInventory();
		player.openInventory(Bukkit.createInventory(player, editicon.getSize(), editicon.getTitle()));
		player.getOpenInventory().getTopInventory().setContents(editicon.getInventory().getContents());
		player.updateInventory();
		editingIcons.put(player, home);
	}
	@EventHandler
	private void inventoryCloseEvent(InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();
		viewingHomes.remove(player);
		editingIcons.remove(player);
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final boolean isViewingHomes = viewingHomes.contains(player);
		if(isViewingHomes || editingIcons.containsKey(player)) {
			event.setCancelled(true);
			player.updateInventory();
			final int slot = event.getRawSlot();
			final ItemStack current = event.getCurrentItem();
			final String click = event.getClick().name();
			final Inventory top = player.getOpenInventory().getTopInventory();
			if(slot < 0 || slot >= top.getSize() || !click.contains("RIGHT") && !click.contains("LEFT") && !click.contains("MIDDLE") || current == null || current.getType().equals(Material.AIR)) {
				return;
			}
			if(isViewingHomes) {
				final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
				final HomeData data = pdata.getHomeData();
				final List<Home> homes = data.getHomes();
				player.closeInventory();
				final Home home = homes.get(slot);
				if(click.contains("LEFT")) {
					teleportToHome(player, home);
				} else if(click.equals("MIDDLE")) {
					final HashMap<String, String> replacements = new HashMap<>();
					replacements.put("{HOME}", home.getName());
					sendStringListMessage(player, getStringList(config, "messages.delete"), replacements);
					data.deleteHome(home);
				} else if(click.contains("RIGHT")) {
					editIcon(player, home);
				}
			} else {
				final Home home = editingIcons.get(player);
				final UMaterial um = UMaterial.match(current);
				final String name = home.getName(), material = um.name();
				home.setIcon(um);
				player.closeInventory();
				final HashMap<String, String> replacements = new HashMap<>();
				replacements.put("{HOME}", name);
				replacements.put("{ICON}", material);
				sendStringListMessage(player, getStringList(config, "messages.save icon"), replacements);
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
			final HomeData data = FileRPPlayer.get(player.getUniqueId()).getHomeData();
			data.setAddedMaxHomes(data.getAddedMaxHomes()+1);
			sendStringListMessage(player, getStringList(config, "messages.unlocked new home slot"), null);
		}
	}
	@EventHandler
	private void factionLeaveEvent(FactionLeaveEvent event) {
		final Player player = event.player;
		final List<Home> homes = FileRPPlayer.get(player.getUniqueId()).getHomeData().getHomes();
		if(homes != null && !homes.isEmpty()) {
			final List<String> msg = getStringList(config, "messages.deleted due to inside a faction claim");
			final HashMap<String, String> replacements = new HashMap<>();
			final List<Chunk> chunks = factions.getRegionalChunks(event.faction);
			for(Home h : new ArrayList<>(homes)) {
				final Location l = h.getLocation();
				if(chunks.contains(l.getChunk())) {
					replacements.put("{HOME}", h.getName());
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
