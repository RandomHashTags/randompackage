package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;
import static me.randomhashtags.randompackage.util.RPFeature.otherdata;

public class CollectionChest {
	public static HashMap<UUID, CollectionChest> chests;

	private UUID uuid;
	private UMaterial filter;
	private String placer;
	private Location location;
	private Inventory inv;
	public CollectionChest(String placer, Location location, UMaterial filter) {
		this(UUID.randomUUID(), placer, location, filter);
	}
	public CollectionChest(UUID uuid, String placer, Location location, UMaterial filter) {
		if(chests == null) chests = new HashMap<>();
		this.uuid = uuid;
		this.placer = placer;
		this.filter = filter;
		this.location = location;
		chests.put(uuid, this);
	}
	public UUID getUUID() { return uuid; }
	public UMaterial getFilter() { return filter; }
	public String getPlacer() { return placer; }
	public Location getLocation() { return location; }
	public Inventory getInventory() {
		final BlockState b = location.getWorld().getBlockAt(location).getState();
		if(inv == null && b instanceof Chest) {
			final Chest c = (Chest) b;
			inv = c.getBlockInventory();
			c.setCustomName("Collection Chest");
			c.update();
			final String s = uuid.toString();
			final ConfigurationSection st = otherdata.getConfigurationSection("collection chests." + s + ".storage");
			if(st != null) {
				for(String i : st.getKeys(false)) {
					ItemStack is;
					try {
						is = otherdata.getItemStack("collection chests." + s + ".storage." + i);
					} catch (Exception e) {
						is = api.d(otherdata, "collection chests." + s + ".storage." + i);
					}
					inv.setItem(Integer.parseInt(i), is);
				}
			}
		}
		return inv;
	}
	public void backup() {
		int i = 0;
		final String u = uuid.toString();
		for(ItemStack is : getInventory().getContents()) {
			if(is != null) {
				otherdata.set("collection chests." + u + ".info", placer + ":" + api.toString(location) + ":" + (filter != null ? filter.name() : "null"));
				otherdata.set("collection chests." + u + ".storage." + i, is.toString());
			}
			i++;
		}
		api.saveOtherData();
	}

	public void setFilter(UMaterial newfilter) {
		filter = newfilter;
	}
	public void destroy() {
		final World w = location.getWorld();
		final Inventory i = getInventory();
		if(i != null) {
			for(ItemStack is : i.getContents()) {
				if(is != null) {
					w.dropItemNaturally(location, is);
				}
			}
		}
		delete();
	}
	public void delete() {
		chests.remove(uuid);
	}
	
	public static CollectionChest valueOf(Block block) {
		if(chests != null) {
			for(CollectionChest cc : chests.values()) {
				if(cc.location.equals(block.getLocation())) {
					return cc;
				}
			}
		}
		return null;
	}
	public static void deleteAll() {
		if(chests != null) {
			for(CollectionChest c : new ArrayList<>(chests.values())) {
				c.delete();
			}
		}
		chests = null;
	}
}
