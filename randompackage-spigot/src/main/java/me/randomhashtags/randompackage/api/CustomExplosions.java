package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.CustomExplosion;
import me.randomhashtags.randompackage.addon.file.FileCustomCreeper;
import me.randomhashtags.randompackage.addon.file.FileCustomTNT;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum CustomExplosions implements RPFeatureSpigot {
	INSTANCE;

	public YamlConfiguration config;
	private List<UMaterial> cannotBreakTNT, cannotBreakCreepers;

	@Override
	public @NotNull Feature get_feature() {
		return Feature.CUSTOM_EXPLOSION;
	}

	@Override
	public void load() {
		save(null, "custom explosions.yml");
		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "custom explosions.yml"));
		cannotBreakTNT = new ArrayList<>();
		cannotBreakCreepers = new ArrayList<>();
		for(String s : config.getStringList("tnt.cannot break")) {
			cannotBreakTNT.add(UMaterial.match(s));
		}
		for(String s : config.getStringList("creepers.cannot break")) {
			cannotBreakCreepers.add(UMaterial.match(s));
		}

		if(!OTHER_YML.getBoolean("saved default custom creepers")) {
			generateDefaultCustomCreepers();
			OTHER_YML.set("saved default custom creepers", true);
			saveOtherData();
		}
		if(!OTHER_YML.getBoolean("saved default custom tnt")) {
			generateDefaultCustomTNT();
			OTHER_YML.set("saved default custom tnt", true);
			saveOtherData();
		}

		for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "custom creepers")) {
			new FileCustomCreeper(f);
		}
		for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "custom tnt")) {
			new FileCustomTNT(f);
		}

		final HashMap<String, Identifiable> explosions = getAll(Feature.CUSTOM_EXPLOSION);
		final List<ItemStack> explosionsList = new ArrayList<>();
		for(Identifiable id : explosions.values()) {
			final CustomExplosion explosion = (CustomExplosion) id;
			explosionsList.add(explosion.getItem());
		}
		addGivedpCategory(explosionsList, UMaterial.TNT, "Custom Explosions", "Givedp: Custom Explosions");

		int loadedPlaced = 0, loadedPrimed = 0, loadedLiving = 0;
		final List<String> placedtnt = OTHER_YML.getStringList("tnt.placed"), primedtnt = OTHER_YML.getStringList("tnt.primed"), livingcreepers = OTHER_YML.getStringList("creepers");
		if(!placedtnt.isEmpty()) {
			for(String s : placedtnt) {
				final Location l = string_to_location(s.split(":")[0]);
				if(l.getWorld().getBlockAt(l).getType().equals(Material.TNT)) {
					FileCustomTNT.PLACED.put(l, (FileCustomTNT) getCustomExplosion("TNT_" + s.split(":")[1]));
					loadedPlaced += 1;
				}
			}
			if(loadedPlaced != 0) {
				sendConsoleMessage("&6[RandomPackage] &aLoaded " + loadedPlaced + " existing placed tnt");
			}
		}
		if(!primedtnt.isEmpty()) {
			for(String s : primedtnt) {
				final String[] values = s.split(":");
				final UUID uuid = UUID.fromString(values[0]);
				final Entity en = get_entity_from_uuid(uuid);
				if(en != null && !en.isDead()) {
					FileCustomTNT.PRIMED.put(uuid, (FileCustomTNT) getCustomExplosion("TNT_" + values[1]));
					loadedPrimed += 1;
				}
			}
			if(loadedPrimed != 0) {
				sendConsoleMessage("&6[RandomPackage] &aLoaded " + loadedPrimed + " existing primed tnt");
			}
		}
		if(!livingcreepers.isEmpty()) {
			for(String s : livingcreepers) {
				final String[] values = s.split(":");
				final UUID uuid = UUID.fromString(values[0]);
				final Entity en = get_entity_from_uuid(uuid);
				if(en != null && !en.isDead()) {
					FileCustomCreeper.LIVING.put(uuid, (FileCustomCreeper) getCustomExplosion("CREEPER_" + values[1]));
					loadedLiving += 1;
				}
			}
			if(loadedLiving != 0) {
				sendConsoleMessage("&6[RandomPackage] &aLoaded " + loadedLiving + " custom living creepers");
			}
		}
	}
	@Override
	public void unload() {
		final HashMap<Location, FileCustomTNT> tnt = FileCustomTNT.PLACED;
		final HashMap<UUID, FileCustomCreeper> creepers = FileCustomCreeper.LIVING;
		final HashMap<UUID, FileCustomTNT> primed = FileCustomTNT.PRIMED;
		OTHER_YML.set("tnt", null);
		OTHER_YML.set("creepers", null);
		final List<String> placedtnt = new ArrayList<>(), primedtnt = new ArrayList<>(), cree = new ArrayList<>();
		if(tnt != null) {
			for(Location l : tnt.keySet()) {
				placedtnt.add(location_to_string(l) + ":" + tnt.get(l).getIdentifier());
			}
		}
		OTHER_YML.set("tnt.placed", placedtnt);
		if(primed != null) {
			for(UUID uuid : primed.keySet()) {
				primedtnt.add(uuid.toString() + ":" + primed.get(uuid).getIdentifier());
			}
		}
		OTHER_YML.set("tnt.primed", primedtnt);
		if(creepers != null) {
			for(UUID uuid : creepers.keySet()) {
				cree.add(uuid.toString() + ":" + creepers.get(uuid).getIdentifier());
			}
		}
		OTHER_YML.set("creepers", cree);
		saveOtherData();

		FileCustomCreeper.LIVING = null;
		FileCustomTNT.PLACED = null;
		FileCustomTNT.PRIMED = null;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void blockBreakEvent(BlockBreakEvent event) {
		final Block block = event.getBlock();
		if(block.getType().equals(Material.TNT)) {
			final Location location = block.getLocation();
			final HashMap<Location, FileCustomTNT> placed = FileCustomTNT.PLACED;
			if(placed != null && placed.containsKey(location)) {
				event.setCancelled(true);
				block.setType(Material.AIR);
				block.getWorld().dropItemNaturally(location, placed.get(location).getItem());
				placed.remove(location);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void blockPlaceEvent(BlockPlaceEvent event) {
		final ItemStack is = event.getItemInHand();
		if(is.getType().equals(Material.TNT) && is.hasItemMeta()) {
			final CustomExplosion explosion = valueOfCustomExplosion(is);
			if(explosion instanceof FileCustomTNT) {
				((FileCustomTNT) explosion).place(event.getBlockPlaced().getLocation());
			}
		}
	}
	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack is = event.getItem();
		if(is != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			final Block block = event.getClickedBlock();
			if(block != null) {
				final Location location = block.getLocation();
				if(block.getType().equals(Material.TNT) && !is.getType().equals(Material.AIR)) {
					final Material material = is.getType();
					final HashMap<Location, FileCustomTNT> placed = FileCustomTNT.PLACED;
					final FileCustomTNT tnt = placed != null ? placed.getOrDefault(location, null) : null;
					if(tnt != null && (material.equals(UMaterial.FIREWORK_STAR.getMaterial()) || material.equals(Material.FLINT_AND_STEEL))) {
						event.setCancelled(true);
						block.setType(Material.AIR);
						tnt.ignite(location);
					}
				} else {
					final CustomExplosion explosion = valueOfCustomExplosion(is);
					if(explosion instanceof FileCustomCreeper) {
						final Location lo = location.clone().add(0.5, 1, 0.5);
						event.setCancelled(true);
						removeItem(event.getPlayer(), is, 1);
						((FileCustomCreeper) explosion).spawn(lo);
					}
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void blockDispenseEvent(BlockDispenseEvent event) {
		final ItemStack is = event.getItem();
		if(is.hasItemMeta()) {
			final CustomExplosion explosion = valueOfCustomExplosion(is);
			if(explosion instanceof FileCustomTNT) {
				event.setCancelled(true);
				final Block block = event.getBlock();
				final Location blockLocation = block.getLocation();
				double x = blockLocation.getBlockX(), y = blockLocation.getBlockY(), z = blockLocation.getBlockZ();
				final Dispenser disp = (Dispenser) block.getState().getData();
				final BlockFace face = disp.getFacing();
				final boolean down = face.equals(BlockFace.DOWN), up = face.equals(BlockFace.UP), north = face.equals(BlockFace.NORTH), east = face.equals(BlockFace.EAST), west = face.equals(BlockFace.WEST), south = face.equals(BlockFace.SOUTH);
				if(down) {
					y -= 1.0;
				} else if(up) {
					y += 1.0;
				} else if(north) {
					z -= 0.5;
				} else if(south) {
					z += 1.5;
				} else if(west) {
					x -= 0.5;
				} else if(east) {
					x += 1.5;
				} else {
					Bukkit.broadcastMessage("[RandomPackage] CustomExplosions -> Different direction! \"" + disp.getFacing().name() + "\"");
					return;
				}
				if(!east && !west) {
					x += 0.5;
				}
				if(!face.name().endsWith("TH")) {
					z += 0.5;
				}
				final Location location = new Location(block.getWorld(), x, y, z);
				((FileCustomTNT) explosion).spawn(location);

				org.bukkit.block.Dispenser dis = (org.bukkit.block.Dispenser) block.getState();
				final Inventory inv = dis.getInventory();
				for(int slot = 0; slot < inv.getSize(); slot++) {
					final ItemStack target = inv.getItem(slot);
					if(target != null && target.isSimilar(is)) {
						final int slotFinal = slot;
						SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
							final ItemStack itemstack = inv.getItem(slotFinal);
							if(itemstack != null) {
								final int amount = itemstack.getAmount();
								if(amount == 1) {
									inv.setItem(slotFinal, new ItemStack(Material.AIR));
								} else {
									target.setAmount(amount - 1);
								}
							}
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
		final Entity entity = event.getEntity();
		final UUID uuid = entity.getUniqueId();
		final HashMap<UUID, FileCustomCreeper> creepers = FileCustomCreeper.LIVING;
		final HashMap<UUID, FileCustomTNT> primed = FileCustomTNT.PRIMED;
		final FileCustomCreeper creeper = creepers != null ? creepers.getOrDefault(uuid, null) : null;
		final FileCustomTNT tnt = creeper == null && primed != null ? primed.getOrDefault(uuid, null) : null;
		if(creeper == null && tnt == null) {
			return;
		}
		(creeper != null ? creeper : tnt).explode(event, entity.getLocation());
	}
}
