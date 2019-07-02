package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomExplosion;
import me.randomhashtags.randompackage.utils.classes.CustomCreeper;
import me.randomhashtags.randompackage.utils.classes.CustomTNT;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
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
				new CustomCreeper(f);
			}
		}
		if(tf.exists()) {
			for(File f : tf.listFiles()) {
				new CustomTNT(f);
			}
		}

		final HashMap<String, AbstractCustomExplosion> e = AbstractCustomExplosion.explosions;
		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (e != null ? e.size() : null) + " Custom Explosions &e(took " + (System.currentTimeMillis()-started) + "ms)");

		final ArrayList<ItemStack> explosions = new ArrayList<>();
		if(e != null) for(AbstractCustomExplosion ae : e.values()) explosions.add(ae.getItem());

		addGivedpCategory(explosions, UMaterial.TNT, "Custom Explosions", "Givedp: Custom Explosions");

		int loadedPlaced = 0, loadedPrimed = 0, loadedLiving = 0;
		final List<String> placedtnt = a.getStringList("tnt.placed"), primedtnt = a.getStringList("tnt.primed"), livingcreepers = a.getStringList("creepers");
		if(placedtnt != null && !placedtnt.isEmpty()) {
			for(String s : placedtnt) {
				final Location l = toLocation(s.split(":")[0]);
				if(l.getWorld().getBlockAt(l).getType().equals(Material.TNT)) {
					CustomTNT.placed.put(l, (CustomTNT) getCustomExplosion(null, "TNT_" + s.split(":")[1]));
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
					CustomTNT.primed.put(u, (CustomTNT) getCustomExplosion(null, "TNT_" + s.split(":")[1]));
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
					CustomCreeper.living.put(u, (CustomCreeper) getCustomExplosion(null, "CREEPER_" + s.split(":")[1]));
					loadedLiving += 1;
				}
			}
			if(loadedLiving != 0) sendConsoleMessage("&6[RandomPackage] &aLoaded " + loadedLiving + " custom living creepers");
		}
	}
	public void unload() {
		config = null;
		cannotBreakTNT = null;
		cannotBreakCreepers = null;
		final HashMap<Location, CustomTNT> tnt = CustomTNT.placed;
		final HashMap<UUID, CustomCreeper> creepers = CustomCreeper.living;
		final HashMap<UUID, CustomTNT> primed = CustomTNT.primed;
		final YamlConfiguration a = otherdata;
		a.set("tnt", null);
		a.set("creepers", null);
		final List<String> placedtnt = new ArrayList<>(), primedtnt = new ArrayList<>(), cree = new ArrayList<>();
		for(Location l : tnt.keySet()) placedtnt.add(toString(l) + ":" + tnt.get(l).getYamlName());
		a.set("tnt.placed", placedtnt);
		for(UUID u : primed.keySet()) primedtnt.add(u.toString() + ":" + primed.get(u).getYamlName());
		a.set("tnt.primed", primedtnt);
		for(UUID u : creepers.keySet()) cree.add(u.toString() + ":" + creepers.get(u).getYamlName());
		a.set("creepers", cree);
		saveOtherData();

		AbstractCustomExplosion.explosions = null;
		CustomCreeper.deleteAll();
		CustomTNT.deleteAll();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void blockBreakEvent(BlockBreakEvent event) {
		final Block b = event.getBlock();
		if(!event.isCancelled() && b.getType().equals(Material.TNT)) {
			final Location l = b.getLocation();
			final HashMap<Location, CustomTNT> p = CustomTNT.placed;
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
			if(i != null && i.getType().equals(Material.TNT) && i.hasItemMeta()) {
				final AbstractCustomExplosion ce = AbstractCustomExplosion.valueOf(i);
				if(ce instanceof CustomTNT) {
					((CustomTNT) ce).place(event.getBlockPlaced().getLocation());
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
				final HashMap<Location, CustomTNT> p = CustomTNT.placed;
				final CustomTNT ce = p != null ? p.getOrDefault(l, null) : null;
				if(ce != null && (t.equals(UMaterial.FIREWORK_STAR.getMaterial()) || t.equals(Material.FLINT_AND_STEEL))) {
					event.setCancelled(true);
					l.getWorld().getBlockAt(l).setType(Material.AIR);
					ce.ignite(l);
				}
			} else {
				final AbstractCustomExplosion c = AbstractCustomExplosion.valueOf(i);
				if(c instanceof CustomCreeper) {
					final Location lo = l.clone().add(0, 1, 0);
					event.setCancelled(true);
					removeItem(event.getPlayer(), i, 1);
					((CustomCreeper) c).spawn(lo);
				}
			}
		}
	}
	@EventHandler
	private void blockDispenseEvent(BlockDispenseEvent event) {
		if(!event.isCancelled()) {
			final ItemStack it = event.getItem();
			if(it != null && it.hasItemMeta()) {
				final AbstractCustomExplosion ce = AbstractCustomExplosion.valueOf(it);
				if(ce instanceof CustomTNT) {
					event.setCancelled(true);
					final Block b = event.getBlock();
					final Location bl = b.getLocation();
					double x = bl.getBlockX(), y = bl.getBlockY(), z = bl.getBlockZ();
					final Dispenser disp = (Dispenser) b.getState().getData();
					final BlockFace bf = disp.getFacing();
					if(bf.equals(BlockFace.DOWN)) y -= 1.0;
					else if(bf.equals(BlockFace.UP)) y += 1.0;
					else if(bf.equals(BlockFace.NORTH)) z -= 0.5;
					else if(bf.equals(BlockFace.SOUTH)) z += 1.5;
					else if(bf.equals(BlockFace.WEST)) x -= 0.5;
					else if(bf.equals(BlockFace.EAST)) x += 1.5;
					else {
						Bukkit.broadcastMessage("[RandomPackage.CustomExplosions] Different direction! \"" + disp.getFacing().name() + "\"");
						return;
					}
					if(!bf.equals(BlockFace.EAST) && !bf.equals(BlockFace.WEST)) x += 0.5;
					if(!bf.name().endsWith("TH")) z += 0.5;
					final Location l = new Location(b.getWorld(), x, y, z);
					((CustomTNT) ce).spawn(l);

					org.bukkit.block.Dispenser dis = (org.bukkit.block.Dispenser) b.getState();
					final Inventory i = dis.getInventory();
					for(int d = 0; d < i.getSize(); d++) {
						if(i.getItem(d) != null && i.getItem(d).hasItemMeta() && i.getItem(d).getItemMeta().equals(event.getItem().getItemMeta())) {
							final int e = d;
							scheduler.scheduleSyncDelayedTask(randompackage, () -> {
								final ItemStack a = i.getItem(e);
								if(a.getAmount() == 1) i.setItem(e, new ItemStack(Material.AIR));
								else                   a.setAmount(a.getAmount() - 1);
								dis.update();
							}, 0);
							return;
						}
					}
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void entityExplodeEvent(EntityExplodeEvent event) {
		if(!event.isCancelled()) {
			final Entity e = event.getEntity();
			final UUID uuid = e.getUniqueId();
			final HashMap<UUID, CustomCreeper> CC = CustomCreeper.living;
			final HashMap<UUID, CustomTNT> CT = CustomTNT.primed;
			final CustomCreeper creeper = CC != null ? CC.getOrDefault(uuid, null) : null;
			final CustomTNT tnt = creeper == null && CT != null ? CT.getOrDefault(uuid, null) : null;
			if(creeper == null && tnt == null) return;
			final Location l = e.getLocation();
			if(creeper != null) creeper.explode(event, l);
			else tnt.explode(event, l);
		}
	}
}
