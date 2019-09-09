package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.CustomExplosion;
import me.randomhashtags.randompackage.util.addon.FileCustomCreeper;
import me.randomhashtags.randompackage.util.addon.FileCustomTNT;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dispenser;

import java.io.File;
import java.util.*;

public class CustomExplosions extends RPFeature {
	private static CustomExplosions instance;
	public static CustomExplosions getCustomExplosions() {
		if(instance == null) instance = new CustomExplosions();
		return instance;
	}
	public YamlConfiguration config;
	private List<UMaterial> cannotBreakTNT, cannotBreakCreepers;

	public String getIdentifier() { return "CUSTOM_EXPLOSIONS"; }
	protected RPFeature getFeature() { return getCustomExplosions(); }
	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "custom explosions.yml");
		config = YamlConfiguration.loadConfiguration(new File(rpd, "custom explosions.yml"));
		cannotBreakTNT = new ArrayList<>();
		cannotBreakCreepers = new ArrayList<>();
		for(String s : config.getStringList("tnt.cannot break")) {
			cannotBreakTNT.add(UMaterial.match(s));
		}
		for(String s : config.getStringList("creepers.cannot break")) {
			cannotBreakCreepers.add(UMaterial.match(s));
		}

		final YamlConfiguration a = otherdata;
		if(!a.getBoolean("saved default custom creepers")) {
			final String[] c = new String[] {"GIGANTIC", "LUCKY", "STUN", "TACTICAL"};
			for(String s : c) save("custom creepers", s + ".yml");
			a.set("saved default custom creepers", true);
			saveOtherData();
		}
		if(!a.getBoolean("saved default custom tnt")) {
			final String[] c = new String[] {"GIGANTIC", "LETHAL", "LUCKY", "MIMIC", "TACTICAL"};
			for(String s : c) save("custom tnt", s + ".yml");
			a.set("saved default custom tnt", true);
			saveOtherData();
		}

		final File cf = new File(rpd + separator + "custom creepers"), tf = new File(rpd + separator + "custom tnt");
		if(cf.exists()) {
			for(File f : cf.listFiles()) {
				new FileCustomCreeper(f);
			}
		}
		if(tf.exists()) {
			for(File f : tf.listFiles()) {
				new FileCustomTNT(f);
			}
		}

		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (explosions != null ? explosions.size() : 0) + " Custom Explosions &e(took " + (System.currentTimeMillis()-started) + "ms)");

		final ArrayList<ItemStack> E = new ArrayList<>();
		if(explosions != null) {
			for(CustomExplosion ae : explosions.values()) {
				E.add(ae.getItem());
			}
		}

		addGivedpCategory(E, UMaterial.TNT, "Custom Explosions", "Givedp: Custom Explosions");

		int loadedPlaced = 0, loadedPrimed = 0, loadedLiving = 0;
		final List<String> placedtnt = a.getStringList("tnt.placed"), primedtnt = a.getStringList("tnt.primed"), livingcreepers = a.getStringList("creepers");
		if(placedtnt != null && !placedtnt.isEmpty()) {
			for(String s : placedtnt) {
				final Location l = toLocation(s.split(":")[0]);
				if(l.getWorld().getBlockAt(l).getType().equals(Material.TNT)) {
					FileCustomTNT.placed.put(l, (FileCustomTNT) getExplosion("TNT_" + s.split(":")[1]));
					loadedPlaced += 1;
				}
			}
			if(loadedPlaced != 0) sendConsoleMessage("&6[RandomPackage] &aLoaded " + loadedPlaced + " existing placed tnt");
		}
		if(primedtnt != null && !primedtnt.isEmpty()) {
			for(String s : primedtnt) {
				final UUID u = UUID.fromString(s.split(":")[0]);
				final Entity en = getEntity(u);
				if(en != null && !en.isDead()) {
					FileCustomTNT.primed.put(u, (FileCustomTNT) getExplosion("TNT_" + s.split(":")[1]));
					loadedPrimed += 1;
				}
			}
			if(loadedPrimed != 0) sendConsoleMessage("&6[RandomPackage] &aLoaded " + loadedPrimed + " existing primed tnt");
		}
		if(livingcreepers != null && !livingcreepers.isEmpty()) {
			for(String s : livingcreepers) {
				final UUID u = UUID.fromString(s.split(":")[0]);
				final Entity en = getEntity(u);
				if(en != null && !en.isDead()) {
					FileCustomCreeper.living.put(u, (FileCustomCreeper) getExplosion("CREEPER_" + s.split(":")[1]));
					loadedLiving += 1;
				}
			}
			if(loadedLiving != 0) sendConsoleMessage("&6[RandomPackage] &aLoaded " + loadedLiving + " custom living creepers");
		}
	}
	public void unload() {
		final HashMap<Location, FileCustomTNT> tnt = FileCustomTNT.placed;
		final HashMap<UUID, FileCustomCreeper> creepers = FileCustomCreeper.living;
		final HashMap<UUID, FileCustomTNT> primed = FileCustomTNT.primed;
		final YamlConfiguration a = otherdata;
		a.set("tnt", null);
		a.set("creepers", null);
		final List<String> placedtnt = new ArrayList<>(), primedtnt = new ArrayList<>(), cree = new ArrayList<>();
		if(tnt != null) {
			for(Location l : tnt.keySet()) placedtnt.add(toString(l) + ":" + tnt.get(l).getIdentifier());
		}
		a.set("tnt.placed", placedtnt);
		if(primed != null) {
			for(UUID u : primed.keySet()) primedtnt.add(u.toString() + ":" + primed.get(u).getIdentifier());
		}
		a.set("tnt.primed", primedtnt);
		if(creepers != null) {
			for(UUID u : creepers.keySet()) cree.add(u.toString() + ":" + creepers.get(u).getIdentifier());
		}
		a.set("creepers", cree);
		saveOtherData();

		explosions = null;
		FileCustomCreeper.living = null;
		FileCustomTNT.placed = null;
		FileCustomTNT.primed = null;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void blockBreakEvent(BlockBreakEvent event) {
		final Block b = event.getBlock();
		if(!event.isCancelled() && b.getType().equals(Material.TNT)) {
			final Location l = b.getLocation();
			final HashMap<Location, FileCustomTNT> p = FileCustomTNT.placed;
			if(p != null && p.containsKey(l)) {
				event.setCancelled(true);
				b.setType(Material.AIR);
				b.getWorld().dropItemNaturally(l, p.get(l).getItem());
				p.remove(l);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void blockPlaceEvent(BlockPlaceEvent event) {
		if(!event.isCancelled()) {
			final ItemStack i = event.getItemInHand();
			if(i.getType().equals(Material.TNT) && i.hasItemMeta()) {
				final CustomExplosion ce = valueOfCustomExplosion(i);
				if(ce instanceof FileCustomTNT) {
					((FileCustomTNT) ce).place(event.getBlockPlaced().getLocation());
				}
			}
		}
	}
	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack i = event.getItem();
		if(i != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			final Block b = event.getClickedBlock();
			final Location l = b.getLocation();
			if(b.getType().equals(Material.TNT) && !i.getType().equals(Material.AIR)) {
				final Material t = i.getType();
				final HashMap<Location, FileCustomTNT> p = FileCustomTNT.placed;
				final FileCustomTNT ce = p != null ? p.getOrDefault(l, null) : null;
				if(ce != null && (t.equals(UMaterial.FIREWORK_STAR.getMaterial()) || t.equals(Material.FLINT_AND_STEEL))) {
					event.setCancelled(true);
					l.getWorld().getBlockAt(l).setType(Material.AIR);
					ce.ignite(l);
				}
			} else {
				final CustomExplosion c = valueOfCustomExplosion(i);
				if(c instanceof FileCustomCreeper) {
					final Location lo = l.clone().add(0, 1, 0);
					event.setCancelled(true);
					removeItem(event.getPlayer(), i, 1);
					((FileCustomCreeper) c).spawn(lo);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void blockDispenseEvent(BlockDispenseEvent event) {
		final ItemStack it = event.getItem();
		if(it.hasItemMeta()) {
			final CustomExplosion ce = valueOfCustomExplosion(it);
			if(ce instanceof FileCustomTNT) {
				event.setCancelled(true);
				final Block b = event.getBlock();
				final Location bl = b.getLocation();
				double x = bl.getBlockX(), y = bl.getBlockY(), z = bl.getBlockZ();
				final Dispenser disp = (Dispenser) b.getState().getData();
				final BlockFace bf = disp.getFacing();
				final boolean down = bf.equals(BlockFace.DOWN), up = bf.equals(BlockFace.UP), north = bf.equals(BlockFace.NORTH), east = bf.equals(BlockFace.EAST), west = bf.equals(BlockFace.WEST), south = bf.equals(BlockFace.SOUTH);
				if(down) y -= 1.0;
				else if(up) y += 1.0;
				else if(north) z -= 0.5;
				else if(south) z += 1.5;
				else if(west) x -= 0.5;
				else if(east) x += 1.5;
				else {
					Bukkit.broadcastMessage("[RandomPackage.CustomExplosions] Different direction! \"" + disp.getFacing().name() + "\"");
					return;
				}
				if(!east && !west) x += 0.5;
				if(!bf.name().endsWith("TH")) z += 0.5;
				final Location l = new Location(b.getWorld(), x, y, z);
				((FileCustomTNT) ce).spawn(l);

				org.bukkit.block.Dispenser dis = (org.bukkit.block.Dispenser) b.getState();
				final Inventory i = dis.getInventory();
				for(int d = 0; d < i.getSize(); d++) {
					final ItemStack target = i.getItem(d);
					if(target != null && target.isSimilar(it)) {
						final int e = d;
						scheduler.scheduleSyncDelayedTask(randompackage, () -> {
							final ItemStack a = i.getItem(e);
							if(a.getAmount() == 1) i.setItem(e, new ItemStack(Material.AIR));
							else                   target.setAmount(a.getAmount() - 1);
							dis.update();
						}, 0);
						return;
					}
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void entityExplodeEvent(EntityExplodeEvent event) {
		final Entity e = event.getEntity();
		final UUID uuid = e.getUniqueId();
		final HashMap<UUID, FileCustomCreeper> CC = FileCustomCreeper.living;
		final HashMap<UUID, FileCustomTNT> CT = FileCustomTNT.primed;
		final FileCustomCreeper creeper = CC != null ? CC.getOrDefault(uuid, null) : null;
		final FileCustomTNT tnt = creeper == null && CT != null ? CT.getOrDefault(uuid, null) : null;
		if(creeper == null && tnt == null) return;
		final Location l = e.getLocation();
		if(creeper != null) creeper.explode(event, l, random);
		else tnt.explode(event, l, random);
	}
}
