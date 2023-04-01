package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.addon.ServerCrateFlare;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FileServerCrateFlareObj implements UVersionableSpigot, NonThrowableJSONBehaviorSpigot, ServerCrateFlare {
	private final FileServerCrate crate;
	private final int spawn_radius, spawn_in_delay, nearby_radius;
	private final boolean drops_from_sky;
	private final List<UMaterial> cannotLandAbove, cannotLandIn;
	private final ItemStack item;
	private final List<String> request_message, nearby_spawn_message;

	public FileServerCrateFlareObj(@NotNull FileServerCrate crate, @NotNull JSONObject crate_json) {
		this.crate = crate;
		final JSONObject flare_json = crate_json.getJSONObject("flare");
		drops_from_sky = parse_boolean_in_json(flare_json, "drops from sky");

		final JSONObject settings_json = flare_json.getJSONObject("settings");
		spawn_radius = parse_int_in_json(flare_json, "spawn radius");
		spawn_in_delay = parse_int_in_json(flare_json, "spawn in delay");
		nearby_radius = parse_int_in_json(flare_json, "nearby radius");
		cannotLandAbove = new ArrayList<>();
		for(String s : parse_list_string_in_json(settings_json, "cannot land above")) {
			cannotLandAbove.add(UMaterial.match(s));
		}
		cannotLandIn = new ArrayList<>();
		for(String s : parse_list_string_in_json(settings_json, "cannot land in")) {
			cannotLandIn.add(UMaterial.match(s));
		}
		item = RandomPackageAPI.INSTANCE.create_item_stack(crate_json, "flare");

		request_message = parse_list_string_in_json(flare_json, "request msg");
		nearby_spawn_message = parse_list_string_in_json(flare_json, "nearby spawn msg");
	}

	@Override
	public @NotNull String getIdentifier() {
		return crate.identifier;
	}

	public @NotNull UMaterial getBlock() {
		return UMaterial.match(crate.getItem().getType().name());
	}
	@Override
	public boolean dropsFromSky() {
		return drops_from_sky;
	}
	public UMaterial getFallingBlock() {
		return null;
	}

	@Override
	public @NotNull List<UMaterial> cannotLandAbove() {
		return cannotLandAbove;
	}
	@Override
	public @NotNull List<UMaterial> cannotLandIn() {
		return cannotLandIn;
	}

	@Override
	public @NotNull ItemStack getItem() {
		return getClone(item);
	}
	@Override
	public @NotNull List<String> getRewards() {
		return List.of();
	}

	@Override
	public int getSpawnRadius() {
		return spawn_radius;
	}
	@Override
	public int getSpawnInDelay() {
		return spawn_in_delay;
	}
	@Override
	public int getNearbyRadius() {
		return nearby_radius;
	}
	@Override
	public List<String> getRequestMsg() {
		return request_message;
	}
	@Override
	public List<String> getNearbySpawnMsg() {
		return nearby_spawn_message;
	}

	@NotNull
	private Location getRandomLocation(World w, int bx, int bz, int sr) {
		final int x = (RANDOM.nextInt(2) == 0 ? 1 : -1)* RANDOM.nextInt(sr), z = (RANDOM.nextInt(2) == 0 ? 1 : -1)* RANDOM.nextInt(sr);
		final Location l = new Location(w, bx+x, 0, bz+z);
		l.setY(w.getHighestBlockYAt(l));
		return l;
	}
	@NotNull
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
}
