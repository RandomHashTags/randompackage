package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.ServerCrateFlare;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class FileServerCrateFlareObj extends UVersion implements ServerCrateFlare {
	private FileServerCrate crate;
	private ItemStack is;
	private List<UMaterial> cannotLandAbove, cannotLandIn;

	public FileServerCrateFlareObj(FileServerCrate crate) {
		this.crate = crate;
	}
	public String getIdentifier() { return crate.getYamlName(); }

	private YamlConfiguration getYaml() { return crate.getYaml(); }
	public UMaterial getBlock() { return UMaterial.match(crate.getItem().getType().name()); }
	public boolean dropsFromSky() { return getYaml().getBoolean("flare.settings.drops from sky"); }
	public UMaterial getFallingBlock() { return null; }

	public List<UMaterial> cannotLandAbove() {
		if(cannotLandAbove == null) {
			cannotLandAbove = new ArrayList<>();
			for(String s : getYaml().getStringList("flare.settings.cannot land above")) {
				cannotLandAbove.add(UMaterial.match(s));
			}
		}
		return cannotLandAbove;
	}
	public List<UMaterial> cannotLandIn() {
		if(cannotLandIn == null) {
			cannotLandIn = new ArrayList<>();
			for(String s : getYaml().getStringList("flare.settings.cannot land in")) {
				cannotLandIn.add(UMaterial.match(s));
			}
		}
		return cannotLandIn;
	}

	public ItemStack getItem() {
		if(is == null) is = api.d(getYaml(), "flare");
		return getClone(is);
	}
	public List<String> getRewards() { return null; }
	public int getSpawnRadius() { return getYaml().getInt("flare.settings.spawn radius"); }
	public int getSpawnInDelay() { return getYaml().getInt("flare.settings.spawn in delay"); }
	public int getNearbyRadius() { return getYaml().getInt("flare.settings.nearby radius"); }
	public List<String> getRequestMsg() { return colorizeListString(getYaml().getStringList("flare.request msg")); }
	public List<String> getNearbySpawnMsg() { return colorizeListString(getYaml().getStringList("flare.nearby spawn msg")); }

	private Location getRandomLocation(World w, int bx, int bz, int sr) {
		final int x = (RANDOM.nextInt(2) == 0 ? 1 : -1)* RANDOM.nextInt(sr), z = (RANDOM.nextInt(2) == 0 ? 1 : -1)* RANDOM.nextInt(sr);
		final Location l = new Location(w, bx+x, 0, bz+z);
		l.setY(w.getHighestBlockYAt(l));
		return l;
	}
	private Location getRandomLocation(World w, int bx, int bz) {
		final int sr = getSpawnRadius();
		for(int i = 1; i <= 100; i++) {
			final Location loc = getRandomLocation(w, bx, bz, sr);
			if(canLand(loc)) {
				return loc;
			}
		}
		return getRandomLocation(w, bz, bz, sr).add(0, 1, 0);
	}

	public Location spawn(Player player, Location requestLocation) {
		final int bx = requestLocation.getBlockX(), by = requestLocation.getBlockY(), bz = requestLocation.getBlockZ();
		final World w = requestLocation.getWorld();
		final Location l = getRandomLocation(w, bx, bz);
		if(l != null) {
			if(player != null) {
				final HashMap<String, String> replacements = new HashMap<>();
				replacements.put("{X}", formatInt(bx));
				replacements.put("{Y}", formatInt(by));
				replacements.put("{Z}", formatInt(bz));
				final List<String> r = getRequestMsg(), n = getNearbySpawnMsg();
				final int ra = getNearbyRadius();
				sendStringListMessage(player, r, replacements);

				for(Entity e : player.getNearbyEntities(ra, ra, ra)) {
					if(e instanceof Player) {
						sendStringListMessage(e, n, replacements);
					}
				}
			}
			SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> w.getBlockAt(l).setType(getBlock().getMaterial()), 20*getSpawnInDelay());
			return l;
		}
		return null;
	}
}
