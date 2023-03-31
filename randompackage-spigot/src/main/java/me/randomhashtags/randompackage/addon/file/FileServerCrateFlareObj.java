package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.addon.ServerCrateFlare;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FileServerCrateFlareObj implements UVersionableSpigot, ServerCrateFlare {
	private final FileServerCrate crate;
	private ItemStack is;
	private List<UMaterial> cannotLandAbove, cannotLandIn;

	public FileServerCrateFlareObj(FileServerCrate crate) {
		this.crate = crate;
	}

	@Override
	public @NotNull String getIdentifier() {
		return crate.getYamlName();
	}

	private YamlConfiguration getYaml() {
		return crate.getYaml();
	}
	public @NotNull UMaterial getBlock() {
		return UMaterial.match(crate.getItem().getType().name());
	}
	public boolean dropsFromSky() {
		return getYaml().getBoolean("flare.settings.drops from sky");
	}
	public UMaterial getFallingBlock() {
		return null;
	}

	public @NotNull List<UMaterial> cannotLandAbove() {
		if(cannotLandAbove == null) {
			cannotLandAbove = new ArrayList<>();
			for(String s : getYaml().getStringList("flare.settings.cannot land above")) {
				cannotLandAbove.add(UMaterial.match(s));
			}
		}
		return cannotLandAbove;
	}
	public @NotNull List<UMaterial> cannotLandIn() {
		if(cannotLandIn == null) {
			cannotLandIn = new ArrayList<>();
			for(String s : getYaml().getStringList("flare.settings.cannot land in")) {
				cannotLandIn.add(UMaterial.match(s));
			}
		}
		return cannotLandIn;
	}

	public @NotNull ItemStack getItem() {
		if(is == null) {
			is = RandomPackageAPI.INSTANCE.createItemStack(getYaml(), "flare");
		}
		return getClone(is);
	}
	public @NotNull List<String> getRewards() {
		return null;
	}

	@Override
	public int getSpawnRadius() {
		return getYaml().getInt("flare.settings.spawn radius");
	}
	@Override
	public int getSpawnInDelay() {
		return getYaml().getInt("flare.settings.spawn in delay");
	}
	@Override
	public int getNearbyRadius() {
		return getYaml().getInt("flare.settings.nearby radius");
	}
	@Override
	public List<String> getRequestMsg() {
		return colorizeListString(getYaml().getStringList("flare.request msg"));
	}
	@Override
	public List<String> getNearbySpawnMsg() {
		return colorizeListString(getYaml().getStringList("flare.nearby spawn msg"));
	}

	private Location getRandomLocation(World w, int bx, int bz, int sr) {
		final int x = (RANDOM.nextInt(2) == 0 ? 1 : -1)* RANDOM.nextInt(sr), z = (RANDOM.nextInt(2) == 0 ? 1 : -1)* RANDOM.nextInt(sr);
		final Location l = new Location(w, bx+x, 0, bz+z);
		l.setY(w.getHighestBlockYAt(l));
		return l;
	}
	private Location getRandomLocation(World w, int bx, int bz) {
		final int spawnRadius = getSpawnRadius();
		for(int i = 1; i <= 100; i++) {
			final Location loc = getRandomLocation(w, bx, bz, spawnRadius);
			if(canLand(loc)) {
				return loc;
			}
		}
		return getRandomLocation(w, bz, bz, spawnRadius).add(0, 1, 0);
	}

	@Override
	public Location spawn(Player player, Location requestLocation) {
		final int blockX = requestLocation.getBlockX(), blockY = requestLocation.getBlockY(), blockZ = requestLocation.getBlockZ();
		final World world = requestLocation.getWorld();
		final Location l = getRandomLocation(world, blockX, blockZ);
		if(l != null) {
			if(player != null) {
				final HashMap<String, String> replacements = new HashMap<>();
				replacements.put("{X}", formatInt(blockX));
				replacements.put("{Y}", formatInt(blockY));
				replacements.put("{Z}", formatInt(blockZ));
				final List<String> r = getRequestMsg(), n = getNearbySpawnMsg();
				final int ra = getNearbyRadius();
				sendStringListMessage(player, r, replacements);

				for(Entity entity : player.getNearbyEntities(ra, ra, ra)) {
					if(entity instanceof Player) {
						sendStringListMessage(entity, n, replacements);
					}
				}
			}
			SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> world.getBlockAt(l).setType(getBlock().getMaterial()), 20*getSpawnInDelay());
			return l;
		}
		return null;
	}
}
