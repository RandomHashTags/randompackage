package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.EnvoyCrate;
import me.randomhashtags.randompackage.addon.file.FileEnvoyCrate;
import me.randomhashtags.randompackage.addon.living.LivingEnvoyCrate;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.PlayerClaimEnvoyCrateEvent;
import me.randomhashtags.randompackage.perms.EnvoyPermission;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.regional.FactionsUUID;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public enum Envoy implements RPFeatureSpigot, CommandExecutor {
	INSTANCE;

	public YamlConfiguration config;

	public ItemStack envoySummon, presetLocationPlacer;
	private int spawnTask, task, totalEnvoys = 0;
	private String default_tier, type;
	public List<Location> preset;
	private List<Player> setting_preset;
	private long nextNaturalEnvoy;

	@Override
	public @NotNull Feature get_feature() {
		return Feature.ENVOY_CRATE;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		final int l = args.length;
		if(l == 0) {
		} else {
		    switch (args[0]) {
				case "spawn":
				case "summon":
				case "begin":
				case "start":
					if(hasPermission(sender, EnvoyPermission.START, true)) {
						spawnEnvoy(colorize(config.getString("messages.default summon type")), false, l == 1 ? type : args[1].toUpperCase());
					}
					break;
				case "stop":
				case "end":
					if(hasPermission(sender, EnvoyPermission.STOP, true)) {
						stop_all_envoys();
					}
					break;
				case "preset":
					if(player != null) {
						enterEditMode(player);
					}
					break;
				default:
					viewHelp(sender);
					break;
			}
		}
		return true;
	}

	@Override
	public void load() {
		save("envoy tiers", "_settings.yml");

		preset = new ArrayList<>();
		setting_preset = new ArrayList<>();

		final List<String> presetLocations = OTHER_YML.getStringList("envoy.preset");
		if(!presetLocations.isEmpty()) {
			for(String s : presetLocations) {
				preset.add(string_to_location(s));
			}
		}
		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "envoy tiers", "_settings.yml"));
		type = config.getString("settings.type");
		envoySummon = createItemStack(config, "items.envoy summon");

		GivedpItem.INSTANCE.items.put("envoysummon", envoySummon);

		presetLocationPlacer = new ItemStack(Material.BEDROCK);
		final ItemMeta itemMeta = presetLocationPlacer.getItemMeta();
		itemMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Preset EnvoyCrate Location");
		itemMeta.setLore(List.of(ChatColor.GRAY + "Place me to add a preset envoy location for", ChatColor.GRAY + "a chance for an EnvoyCrate to spawn at this location."));
		presetLocationPlacer.setItemMeta(itemMeta);

		if(!OTHER_YML.getBoolean("saved default envoy tiers")) {
			generateDefaultEnvoyTiers();
			OTHER_YML.set("saved default envoy tiers", true);
			saveOtherData();
		}

		final List<ItemStack> tiers = new ArrayList<>();
		for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "envoy tiers")) {
			if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
				tiers.add(new FileEnvoyCrate(f).getItem());
			}
		}
		default_tier = config.getString("settings.default tier");
		addGivedpCategory(tiers, UMaterial.ENDER_CHEST, "Envoy Tiers", "Givedp: Envoy Tiers");
		final String defaultSummonType = colorize(config.getString("messages.default summon type"));

		final long next = getRandomTime();
		nextNaturalEnvoy = System.currentTimeMillis()+next*1000;
		spawnTask = SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> spawnEnvoy(defaultSummonType, true, type), next);

		task = SCHEDULER.scheduleSyncRepeatingTask(RANDOM_PACKAGE, () -> {
			final HashMap<Integer, HashMap<Location, LivingEnvoyCrate>> living = LivingEnvoyCrate.LIVING;
			if(living != null) {
				for(int i : living.keySet()) {
					final HashMap<Location, LivingEnvoyCrate> w = living.get(i);
					for(Location l : w.keySet()) {
						spawnFirework(w.get(l).getType().getFirework(), l);
					}
				}
			}
		}, 0, 20L * config.getInt("settings.firework delay"));
	}
	@Override
	public void unload() {
		final List<String> preset = new ArrayList<>();
		for(Location location : this.preset) {
			preset.add(location_to_string(location));
		}
		OTHER_YML.set("envoy.preset", preset);
		saveOtherData();
		if(!setting_preset.isEmpty()) {
			for(Player player : setting_preset) {
				player.getInventory().remove(presetLocationPlacer);
			}
			for(Location l : this.preset) {
				l.getWorld().getBlockAt(l).setType(Material.AIR);
			}
		}
		SCHEDULER.cancelTask(spawnTask);
		SCHEDULER.cancelTask(task);
		stop_all_envoys();
		GivedpItem.INSTANCE.items.remove("envoysummon");
	}

	public long getNextNaturalEnvoy() {
		return nextNaturalEnvoy;
	}
	@NotNull
	public String getUntilNextNaturalEnvoy() {
		return getRemainingTime(nextNaturalEnvoy - System.currentTimeMillis());
	}

	public void stop_all_envoys() {
		final HashMap<Integer, HashMap<Location, LivingEnvoyCrate>> living = LivingEnvoyCrate.LIVING;
		if(living != null) {
			for(int i : living.keySet()) {
				stop_envoy(i, false);
			}
		}
	}
	public void stop_envoy(int envoyID, boolean dropItems) {
		final HashMap<Integer, HashMap<Location, LivingEnvoyCrate>> envoys = LivingEnvoyCrate.LIVING;
		if(envoys != null) {
			final HashMap<Location, LivingEnvoyCrate> chests = new HashMap<>(envoys.get(envoyID));
			for(Location loc : chests.keySet()) {
				chests.get(loc).delete(dropItems);
			}
		}
	}
	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack is = event.getItem();
		final EnvoyCrate crate = valueOfEnvoyCrate(is);
		final Player player = event.getPlayer();
		if(crate != null) {
			event.setCancelled(true);
			player.updateInventory();
			removeItem(player, is, 1);
			final List<String> rewards = crate.getRandomRewards();
			for(String s : rewards) {
				giveItem(player, createItemStack(null, s));
			}
		} else if(is != null && is.hasItemMeta() && is.getItemMeta().equals(envoySummon.getItemMeta())) {
			event.setCancelled(true);
			player.updateInventory();
			removeItem(player, is, 1);
			spawnEnvoy(getString(config, "messages.item summon type").replace("{PLAYER}", player.getName()), false, type);
		} else if(event.getClickedBlock() != null) {
			final Location l = event.getClickedBlock().getLocation();
			final LivingEnvoyCrate livingCrate = LivingEnvoyCrate.valueOf(l);
			if(livingCrate != null) {
				final PlayerClaimEnvoyCrateEvent e = new PlayerClaimEnvoyCrateEvent(player, l, livingCrate);
				PLUGIN_MANAGER.callEvent(e);
				if(!e.isCancelled()) {
					event.setCancelled(true);
					player.updateInventory();
					livingCrate.delete(true);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockPlaceEvent(BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		if(setting_preset.contains(player) && player.getItemInHand().equals(presetLocationPlacer)) {
			preset.add(event.getBlockPlaced().getLocation());
		}
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void blockBreakEvent(BlockBreakEvent event) {
		if(setting_preset.contains(event.getPlayer())) {
			preset.remove(event.getBlock().getLocation());
		}
	}

    public void enterEditMode(@NotNull Player player) {
		if(hasPermission(player, EnvoyPermission.TOGGLE_EDIT_PRESET, true)) {
			final PlayerInventory inv = player.getInventory();
			final boolean viewing = setting_preset.contains(player);
			final Material mat = viewing ? Material.AIR : Material.BEDROCK;
			if(!viewing) {
				setting_preset.add(player);
				inv.addItem(presetLocationPlacer);
			} else {
				setting_preset.remove(player);
				inv.remove(presetLocationPlacer);
			}
			if(setting_preset.isEmpty()) {
				for(Location l : preset) {
					l.getWorld().getBlockAt(l).setType(mat);
				}
			}
		}
    }

	public void spawnEnvoy(@NotNull String type, int amount) {
		final Random random = new Random();
		final int despawn = config.getInt("settings.availability");
		switch (type.toUpperCase()) {
			case "WARZONE":
				if(RegionalAPI.INSTANCE.hookedFactionsUUID()) {
					final List<Chunk> chunks = FactionsUUID.INSTANCE.getRegionalChunks("WarZone");
					if(!chunks.isEmpty()) {
						final int chunks_size = chunks.size();
						for(int i = 1; i <= amount; i++) {
							final List<Location> chunk_locations = getChunkLocations(chunks.get(random.nextInt(chunks_size)));
							final EnvoyCrate crate = getRandomCrate(true, default_tier);
							final Location loc = getRandomLocation(random, chunk_locations), newl = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ());
							loc.getChunk().load();
							LivingEnvoyCrate lec = LivingEnvoyCrate.valueOf(newl);
							if(lec == null && crate.canLand(loc)) {
								lec = new LivingEnvoyCrate(totalEnvoys, crate, loc);
								lec.shoot_firework();
							} else {
								i -= 1;
							}
						}
					}
				} else {
					// TODO
				}
				break;
			case "PRESET":
				final List<Location> preset = new ArrayList<>(this.preset);
				for(int i = 1; i <= amount; i++) {
					final Location loc = preset.get(random.nextInt(preset.size()));
					loc.getChunk().load();
					final World world = loc.getWorld();
					final Location newl = new Location(world, loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ());
					final EnvoyCrate crate = getRandomCrate(true, default_tier);
					LivingEnvoyCrate lec = LivingEnvoyCrate.valueOf(newl);
					if(lec == null && crate.canLand(loc)) {
						lec = new LivingEnvoyCrate(totalEnvoys, crate, loc);
						lec.shoot_firework();
					} else {
						i -= 1;
					}
					preset.remove(loc);
				}
				break;
			default:
				break;
		}
		final int t = totalEnvoys;
		SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> stop_envoy(t, false), 20L * despawn);
		totalEnvoys += 1;
	}
	public void spawnEnvoy(String summonType, boolean natural, String where) {
		for(String string : getStringList(config, "messages.broadcast")) {
			Bukkit.broadcastMessage(string.replace("{SUMMON_TYPE}", summonType));
		}
		spawnEnvoy(where, getRandomAmountSpawned());
		if(natural) {
			final long next = getRandomTime();
			nextNaturalEnvoy = System.currentTimeMillis()+next*1000;
			SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> spawnEnvoy(getString(config, "messages.default summon type"), true, where), next);
		}
	}
	private int getRandomTime() {
		final String repeatTime = getString(config, "settings.repeats");
		final int min = repeatTime.contains("-") ? Integer.parseInt(repeatTime.split("-")[0]) : 0, t = repeatTime.contains("-") ? min+ RANDOM.nextInt(Integer.parseInt(repeatTime.split("-")[1])-min+1) : Integer.parseInt(repeatTime);
		return t*20;
	}
	@NotNull
	private Location getRandomLocation(Random random, List<Location> locs) {
		final Location rl = locs.get(random.nextInt(locs.size()));
		final World w = rl.getWorld();
		final int x = rl.getBlockX(), z = rl.getBlockZ();
		return new Location(w, x, w.getHighestBlockYAt(x, z), z);
	}
	private int getRandomAmountSpawned() {
		final String as = getString(config, "settings.amount spawned");
		final String[] s = as.split("-");
		final boolean hyphen = as.contains("-");
		final int min = Integer.parseInt(hyphen ? s[0] : as);
		return hyphen ? min+ RANDOM.nextInt(Integer.parseInt(s[1])-min+1) : min;
	}
	public void viewHelp(@NotNull CommandSender sender) {
		if(hasPermission(sender, EnvoyPermission.VIEW_HELP, true)) {
			sendStringListMessage(sender, getStringList(config, "messages.envoy help"), null);
		}
	}

	@Nullable
	public EnvoyCrate valueOfEnvoyCrate(ItemStack is) {
		if(is != null && is.hasItemMeta()) {
			for(EnvoyCrate crate : getAllEnvoyCrates().values()) {
				if(is.isSimilar(crate.getItem())) {
					return crate;
				}
			}
		}
		return null;
	}
	@Nullable
	public EnvoyCrate getRandomCrate(boolean useChances, String defaultTier) {
		if(useChances) {
			for(EnvoyCrate crate : getAllEnvoyCrates().values()) {
				if(RANDOM.nextInt(100) <= crate.getChance()) {
					return crate;
				}
			}
		}
		return getEnvoyCrate(defaultTier);
	}
}
