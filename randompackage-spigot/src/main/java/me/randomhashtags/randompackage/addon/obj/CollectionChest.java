package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.RandomPackageAPI;
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

import static me.randomhashtags.randompackage.util.RPFeatureSpigot.OTHER_YML;

public final class CollectionChest {
	public static HashMap<UUID, CollectionChest> CHESTS;

	private final UUID uuid;
	private UMaterial filter;
	private final String placer;
	private final Location location;
	private Inventory inv;

	public CollectionChest(String placer, Location location, UMaterial filter) {
		this(UUID.randomUUID(), placer, location, filter);
	}
	public CollectionChest(UUID uuid, String placer, Location location, UMaterial filter) {
		if(CHESTS == null) {
			CHESTS = new HashMap<>();
		}
		this.uuid = uuid;
		this.placer = placer;
		this.filter = filter;
		this.location = location;
		CHESTS.put(uuid, this);
	}

	public UUID getUUID() {
		return uuid;
	}
	public UMaterial getFilter() {
		return filter;
	}
	public String getPlacer() {
		return placer;
	}
	public Location getLocation() {
		return location;
	}
	public Inventory getInventory() {
		final BlockState blockState = location.getWorld().getBlockAt(location).getState();
		if(inv == null && blockState instanceof Chest) {
			final Chest chest = (Chest) blockState;
			inv = chest.getBlockInventory();
			chest.setCustomName("Collection Chest");
			chest.update();
			final String uuidString = uuid.toString();
			final ConfigurationSection st = OTHER_YML.getConfigurationSection("collection chests." + uuidString + ".storage");
			if(st != null) {
				final RandomPackageAPI api = RandomPackageAPI.INSTANCE;
				for(String i : st.getKeys(false)) {
					ItemStack is;
					try {
						is = OTHER_YML.getItemStack("collection chests." + uuidString + ".storage." + i);
					} catch (Exception e) {
						is = api.createItemStack(OTHER_YML, "collection chests." + uuidString + ".storage." + i);
					}
					inv.setItem(Integer.parseInt(i), is);
				}
			}
		}
		return inv;
	}
	public void backup() {
		int i = 0;
		final String uuidString = uuid.toString();
		final RandomPackageAPI api = RandomPackageAPI.INSTANCE;
		for(ItemStack is : getInventory().getContents()) {
			if(is != null) {
				OTHER_YML.set("collection chests." + uuidString + ".info", placer + ":" + api.location_to_string(location) + ":" + (filter != null ? filter.name() : "null"));
				OTHER_YML.set("collection chests." + uuidString + ".storage." + i, is.toString());
			}
			i++;
		}
		api.saveOtherData();
	}

	public void setFilter(UMaterial newfilter) {
		filter = newfilter;
	}
	public void destroy() {
		final World world = location.getWorld();
		final Inventory inv = getInventory();
		if(inv != null) {
			for(ItemStack is : inv.getContents()) {
				if(is != null) {
					world.dropItemNaturally(location, is);
				}
			}
		}
		delete();
	}
	public void delete() {
		CHESTS.remove(uuid);
	}
	
	public static CollectionChest valueOf(Block block) {
		if(CHESTS != null) {
			for(CollectionChest cc : CHESTS.values()) {
				if(cc.location.equals(block.getLocation())) {
					return cc;
				}
			}
		}
		return null;
	}
	public static void deleteAll() {
		if(CHESTS != null) {
			for(CollectionChest c : new ArrayList<>(CHESTS.values())) {
				c.delete();
			}
		}
		CHESTS = null;
	}
}
