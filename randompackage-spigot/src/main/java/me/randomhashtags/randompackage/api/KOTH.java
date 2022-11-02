package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.event.KothCaptureEvent;
import me.randomhashtags.randompackage.perms.KOTHPermission;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public enum KOTH implements RPFeatureSpigot, CommandExecutor {
	INSTANCE;

	public YamlConfiguration config;
	private UInventory lootbagInv;
	private int scorestart, startCapCountdown = -1, scoreboard;
	
	public ItemStack lootbag;
	public DisplaySlot displaySlot;
	public String kothtitle, kothname, status;
	public Location teleportLocation, center;
	private List<String> captured, limitedcommands, lootbagRewards;
	
	public int captureTime = 0, captureRadius = 0;
	
	public long started = -1;
	
	private String rewardformat;
	private long cappingStartedTime = -1, capturedTime = -1;
	public Player currentPlayerCapturing, previouscapturer;

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		switch (args.length) {
			case 0:
				viewStatus(sender);
				break;
			default:
				switch (args[0]) {
					case "stop":
						if(hasPermission(sender, KOTHPermission.COMMAND_STOP, true)) {
							stopKOTH();
						}
						break;
					case "start":
						if(hasPermission(sender, KOTHPermission.COMMAND_START, true)) {
							startKOTH();
						}
						break;
					case "teleport":
					case "tp":
					case "warp":
						if(player != null) {
							teleportToKOTH(player);
						}
						break;
					case "loot":
						if(player != null) {
							previewLootbag(player);
						}
						break;
					case "setcenter":
						if(player != null) {
							setCenter(player, player.getLocation());
						}
						break;
				}
				break;
		}
		return true;
	}

	@Override
	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "koth.yml");

		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "koth.yml"));
		lootbagInv = new UInventory(null, config.getInt("items.lootbag.size"), colorize(config.getString("items.lootbag.title")));

		lootbag = createItemStack(config, "items.lootbag");
		GivedpItem.INSTANCE.items.put("kothlootbag", lootbag);

		displaySlot = DisplaySlot.valueOf(config.getString("settings.scoreboards.display slot").toUpperCase());
		kothtitle = colorize(config.getString("settings.scoreboards.title"));
		kothname = colorize(config.getString("settings.name"));

		status = "Not Active";

		teleportLocation = OTHER_YML.getString("koth.tp") != null && !OTHER_YML.getString("koth.tp").equals("") ? toLocation(OTHER_YML.getString("koth.tp")) : null;
		captureTime = config.getInt("settings.time to cap");
		startCapCountdown = config.getInt("settings.start cap countdown");
		captureRadius = config.getInt("settings.capture radius");
		captured = getStringList(config, "messages.captured");
		rewardformat = colorize(config.getString("messages.reward format"));
		scorestart = config.getInt("settings.scoreboards.score start");

		limitedcommands = config.getStringList("limited commands");
		lootbagRewards = config.getStringList("items.lootbag.rewards");

		final String center = OTHER_YML.getString("koth.center");
		if(center != null && !center.equals("")) {
			this.center = toLocation(center);
			startKOTH();
		}

		sendConsoleDidLoadFeature("Koth of the Hill", started);
	}
	@Override
	public void unload() {
		OTHER_YML.set("koth.center", center != null ? toString(center) : "");
		OTHER_YML.set("koth.tp", teleportLocation != null ? toString(teleportLocation) : "");
		saveOtherData();

		if(center != null && !status.equals("STOPPED")) {
			for(Player player : getWorldPlayers(center)) {
				player.setScoreboard(SCOREBOARD_MANAGER.getNewScoreboard());
			}
		}
		stopKOTH();
	}
	
	public List<ItemStack> getRandomLootbagContents() {
		final List<ItemStack> loot = new ArrayList<>();
		for(String reward : lootbagRewards) {
			final String rewardLC = reward.toLowerCase();
			final boolean isChance1 = rewardLC.contains(";chance"), isChance2 = rewardLC.startsWith("chance=");
			final String[] values = isChance2 ? rewardLC.split("chance=") : isChance1 ? rewardLC.split(";chance=") : null;
			final int chance = values != null ? Integer.parseInt(values[1].split("-")[0]) : 100;
			if(RANDOM.nextInt(100) < chance) {
				final String original = isChance2 ? reward.split("chance=" + chance + "->")[1] : isChance1 ? reward.split(";chance=")[0] : reward;
				String actualReward = isChance2 ? rewardLC.split("chance=" + chance + "->")[1] : isChance1 ? rewardLC.split(";chance=")[0] : reward;
				if(actualReward.contains("||")) {
					final String[] valueArray = actualReward.split("\\|\\|");
					actualReward = original.split("\\|\\|")[RANDOM.nextInt(valueArray.length)];
				}
				final ItemStack is = createItemStack(null, actualReward);
				if(is != null && !is.getType().equals(Material.AIR)) {
					loot.add(is);
				}
			}
		}
		return loot;
	}
	public void previewLootbag(@NotNull Player player) {
		if(hasPermission(player, KOTHPermission.PREVIEW_LOOT, true)) {
			player.openInventory(Bukkit.createInventory(player, lootbagInv.getSize(), lootbagInv.getTitle()));
			final Inventory top = player.getOpenInventory().getTopInventory();
			final List<ItemStack> lootbag = getRandomLootbagContents();
			final String n = player.getName();
			for(ItemStack is : lootbag) {
			    final int f = top.firstEmpty();
				if(is != null && !(f < 0)) {
				    final ItemStack item = is;
				    final ItemMeta itemMeta = item.getItemMeta();
				    if(itemMeta != null && itemMeta.hasLore()) {
				    	final List<String> lore = new ArrayList<>();
				        for(String s : itemMeta.getLore()) {
				            lore.add(s.replace("{PLAYER}", n).replace("{UNLOCKED_BY}", n));
                        }
				        itemMeta.setLore(lore);
				        item.setItemMeta(itemMeta);
                    }
					top.setItem(f, item);
				}
			}
			player.updateInventory();
		}
	}
	public long getTimeLeft() {
		final int i = captureTime * 1000;
		return cappingStartedTime > 0 ? cappingStartedTime+i-System.currentTimeMillis() : i;
	}
	public long getRuntime() {
		return System.currentTimeMillis()-started;
	}
	public void viewStatus(@NotNull CommandSender sender) {
		if(hasPermission(sender, KOTHPermission.VIEW, true)) {
			final boolean captured = status.equals("CAPTURED"), stopped = status.equals("STOPPED");
			final String status = config.getString("messages.status." + (center == null || captured || stopped ? "closed" : "open")),
					name = currentPlayerCapturing != null ? currentPlayerCapturing.getName() : "",
					flag = config.getString("messages.flag." + (captured || stopped ? "captured" : currentPlayerCapturing == null ? "uncontested" : "capturing"));

			String faction = currentPlayerCapturing != null ? RegionalAPI.INSTANCE.getFactionTag(currentPlayerCapturing.getUniqueId()) : "";
			if(faction != null && !faction.equals("")) {
				faction = faction + " ";
			}

			final String join = getString(config, "messages.join event"), timeleft = getRemainingTime(getTimeLeft()), runtime = getRemainingTime(getRuntime());
			final String players = center != null ? formatInt(center.getWorld().getNearbyEntities(center, captureRadius, captureRadius, captureRadius).size()) : "0";

			for(String string : getStringList(config, "messages.command")) {
				String i = string;
				if(i.contains("{STATUS}")) {
					i = i.replace("{STATUS}", center != null ? status : "&c&lNOT SETUP");
				}
				if(i.contains("{PLAYERS}")) {
					i = center != null ? i.replace("{PLAYERS}", players) : null;
				}
				if(i != null) {
					if(captured) {
						if(i.contains("{CAPTURED_BY}")) {
							i = i.replace("{CAPTURED_BY}", name);
						}
						if(i.contains("{NEXT_KOTH_TIME}")) {
							i = i.replace("{NEXT_KOTH_TIME}", "&f&lUnspecified");
						}
						if(i.contains("{RUNTIME}") || i.contains("{JOIN_EVENT}") || i.contains("{TIME_LEFT}")) {
							i = null;
						}

					} else if(stopped) {
						if(i.contains("{NEXT_KOTH_TIME}")) {
							i = i.replace("{NEXT_KOTH_TIME}", "");
						}
						if(i.contains("{RUNTIME}") || i.contains("JOIN_EVENT") || i.contains("{FLAG}") || i.contains("{FACTION}") || i.contains("{TIME_LEFT}") || i.contains("{CAPTURED_BY}") || i.contains("{PLAYERS}")) {
							i = null;
						}

					} else if(this.status.equals("ACTIVE")) {
						if(i.contains("{RUNTIME}"))   i = i.replace("{RUNTIME}", runtime);
						if(i.contains("{JOIN_EVENT}"))i = i.replace("{JOIN_EVENT}", join);
						if(i.contains("{PLAYER}"))    i = i.replace("{PLAYER}", name);
						if(i.contains("{FACTION}"))   i = i.replace("{FACTION}", faction);
						if(i.contains("{TIME_LEFT}")) i = i.replace("{TIME_LEFT}", timeleft);
						if(i.contains("{FLAG}"))      i = i.replace("{FLAG}", flag);
						if(i.contains("{NEXT_KOTH_TIME}") || i.contains("{CAPTURED_BY}")) {
							i = null;
						}

					} else {
						if(i.contains("{RUNTIME}") || i .contains("{JOIN_EVENT}") || i.contains("{PLAYER}") || i.contains("FACTION") || i.contains("{TIME_LEFT}") || i.contains("{FLAG}") || i.contains("{NEXT_KOTH_TIME}") || i.contains("{CAPTURED_BY}")) {
							i = null;
						}
					}
					if(i != null) {
						sender.sendMessage(colorize(i));
					}
				}
			}
		}
	}
	public void setCenter(@NotNull CommandSender sender, @NotNull Location loc) {
		if(hasPermission(sender, KOTHPermission.SET_CENTER, true)) {
			center = loc;
			final HashMap<String, String> replacements = new HashMap<>();
			replacements.put("{LOCATION}", loc.getBlockX() + "x, " + loc.getBlockY() + "y, " + loc.getBlockZ());
			sendStringListMessage(sender, getStringList(config, "messages.set center"), replacements);
		}
	}
	
	public void startKOTH() {
		started = System.currentTimeMillis();
		status = "ACTIVE";
		if(center != null) {
			scoreboard = SCHEDULER.scheduleSyncRepeatingTask(RANDOM_PACKAGE, () -> {
				final boolean isCaptured = status.equals("CAPTURED");
				setScoreboard(isCaptured);
				if(isCaptured) {
					SCHEDULER.cancelTask(scoreboard);
				}
			}, 0, 20);
		}
	}
	public void stopKOTH() {
		status = "STOPPED";
		SCHEDULER.cancelTask(scoreboard);
		currentPlayerCapturing = null;
		cappingStartedTime = -1;
		capturedTime = -1;
		if(center != null) {
			for(Player player : getWorldPlayers(center)) {
				player.setScoreboard(SCOREBOARD_MANAGER.getNewScoreboard());
			}
		}
	}

	public void broadcastStartCapping() {
		final String time = getRemainingTime(getTimeLeft()), capturingPlayer = currentPlayerCapturing.getName();
		final List<String> msg = getStringList(config, "messages.start capping");
		final HashMap<String, String> replacements = new HashMap<>();
		replacements.put("{PLAYER}", capturingPlayer);
		replacements.put("{TIME}", time);
		for(Player player : getWorldPlayers(center)) {
			sendStringListMessage(player, msg, replacements);
		}
	}
	public void broadcastCapping() {
		final String timeLeft = getRemainingTime(getTimeLeft());
		final List<String> msg = getStringList(config, "messages.capping");
		for(Player player : getWorldPlayers(center)) {
			for(String string : msg) {
				if(string.contains("{PLAYER}")) string = string.replace("{PLAYER}", currentPlayerCapturing.getName());
				if(string.contains("{TIME}")) string = string.replace("{TIME}", timeLeft);
				player.sendMessage(string);
			}
		}
	}
	public void broadcastNoLongerCapping() {
		final List<String> msg = getStringList(config, "messages.no longer capping");
		final String p = previouscapturer.getName();
		for(Player player : getWorldPlayers(center)) {
			for(String string : msg) {
				if(string.contains("{PLAYER}")) string = string.replace("{PLAYER}", p);
				player.sendMessage(string);
			}
		}
	}

	private void setScoreboard(boolean captured) {
		if(center == null) {
			return;
		}
		final long currentTime = System.currentTimeMillis();
		final boolean isCapturing = currentPlayerCapturing != null;
		final long captureTimeLeft = cappingStartedTime > 0 && isCapturing ? cappingStartedTime+captureTime*1000-currentTime : captureTime*1000;
		final String timeLeft = getRemainingTime(captureTimeLeft);
		final boolean isStopped = status.equals("STOPPED"), isCaptured = status.equals("CAPTURED");
		if(isStopped) {
			cappingStartedTime = currentTime;
			currentPlayerCapturing = null;
			previouscapturer = null;
		} else {
			previouscapturer = currentPlayerCapturing;
		}

		final Collection<Player> kothPlayers = getWorldPlayers(center);
		String closestPlayerToKOTH = null;
		int distance = captureRadius;

		if(!isStopped) {
			if(!isCaptured && isCapturing) {
				if(captureTimeLeft <= 0) {
					final KothCaptureEvent captureEvent = new KothCaptureEvent(currentPlayerCapturing);
					PLUGIN_MANAGER.callEvent(captureEvent);
					if(!captureEvent.isCancelled()) {
						final String currentCapturer = currentPlayerCapturing.getName();
						status = "CAPTURED";
						for(Player player : kothPlayers) {
							for(String string : this.captured) {
								player.sendMessage(string.replace("{PLAYER}", currentCapturer));
							}
						}
						final ItemStack item = lootbag.clone();
						final ItemMeta itemMeta = item.getItemMeta();
						final List<String> lore = new ArrayList<>();
						for(String string : itemMeta.getLore()) {
							lore.add(string.replace("{PLAYER}", currentCapturer));
						}
						itemMeta.setLore(lore);
						item.setItemMeta(itemMeta);
						giveItem(currentPlayerCapturing, item.clone());
						return;
					}
				} else if(captureTimeLeft/1000 <= startCapCountdown) {
					broadcastCapping();
				}
			}

			final Collection<Entity> nearby = center.getWorld().getNearbyEntities(center, captureRadius, captureRadius, captureRadius);
			if(isCapturing && nearby.contains(currentPlayerCapturing) && (int) currentPlayerCapturing.getLocation().distance(center) <= captureRadius) {
				closestPlayerToKOTH = currentPlayerCapturing.getName();
			} else {
				for(Entity entity : nearby) {
					if(entity instanceof Player && (int) entity.getLocation().distance(center) <= distance) {
						currentPlayerCapturing = (Player) entity;
						cappingStartedTime = currentTime;
						closestPlayerToKOTH = entity.getName();
						distance = (int) entity.getLocation().distance(center);
						if(previouscapturer != null) {
							broadcastNoLongerCapping();
						}
						broadcastStartCapping();
					}
				}
			}
			if(closestPlayerToKOTH == null) {
				closestPlayerToKOTH = "N/A";
				cappingStartedTime = -1;
				if(isCapturing) {
					broadcastNoLongerCapping();
				}
				currentPlayerCapturing = null;
			}
		} else {
			closestPlayerToKOTH = "N/A";
		}

		final List<String> scoreboardStatus = getStringList(config, "settings.scoreboards." + (captured ? "captured" : "capping"));
		for(Player player : kothPlayers) {
			final Scoreboard scoreboard = SCOREBOARD_MANAGER.getNewScoreboard();
			final Objective obj = scoreboard.registerNewObjective("config", "dummy");
			obj.setDisplayName(kothtitle);
			obj.setDisplaySlot(displaySlot);
			final String dis = Double.toString(player.getLocation().distance(center)).split("\\.")[0];
			for(int i = 0; i < scoreboardStatus.size(); i++) {
				final String score = scoreboardStatus.get(i).replace("{DISTANCE}", dis).replace("{TIME}", timeLeft).replace("{PLAYER}", closestPlayerToKOTH);
				obj.getScore(score).setScore(scorestart-i);
			}
			player.setScoreboard(scoreboard);
		}
	}
	public void teleportToKOTH(@NotNull Player player) {
		if(hasPermission(player, KOTHPermission.TELEPORT, true) && teleportLocation != null) {
			final boolean captured = status.equals("CAPTURED");
			sendStringListMessage(player, null, getStringList(config, "messages." + (captured ? "already capped" : "teleport")), 0, null);
			if(!captured) {
				player.teleport(teleportLocation, TeleportCause.PLUGIN);
			}
		}
	}
	private void sendStringListMessage(Player sender, Player target, List<String> message, int number, Location location) {
		final HashMap<String, String> replacements = new HashMap<>();
		replacements.put("{AMOUNT}", formatInt(number));
		replacements.put("{SENDER}", sender.getName());
		replacements.put("{TARGET}", target != null ? target.getName() : "null");
		replacements.put("{LOCATION}", location.getBlockX() + "x " + location.getBlockY() + "y " + location.getBlockZ() + "z");
		replacements.put("{KOTH}", kothname);
		sendStringListMessage(sender, message, replacements);
	}

	@EventHandler
	private void playerJoinEvent(PlayerJoinEvent event) {
		if(status.equals("ACTIVE")) {
			sendStringListMessage(event.getPlayer(), getStringList(config, "messages.event running"), null);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void playerTeleportEvent(PlayerTeleportEvent event) {
		final World world = center != null ? center.getWorld() : null;
		if(world != null) {
			final Player player = event.getPlayer();
			final boolean isStopped = status.equals("STOPPED");
			if(event.getTo().getWorld().equals(world) && event.getCause().equals(TeleportCause.COMMAND) && !player.hasPermission(KOTHPermission.TELEPORT_BYPASS) && (isStopped || status.equals("CAPTURED"))) {
				event.setCancelled(true);
				sendStringListMessage(player, null, getStringList(config, "messages." + (isStopped ? "no event running" : "already capped")), -1, null);
			} else if(event.getFrom().getWorld().equals(world)) {
				player.setScoreboard(SCOREBOARD_MANAGER.getNewScoreboard());
			}
		}
	}
	@EventHandler
	private void playerQuitEvent(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		if(center != null && player.getWorld().equals(center.getWorld())) {
			player.setScoreboard(SCOREBOARD_MANAGER.getNewScoreboard());
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();
		if(!status.equals("STOPPED") && !limitedcommands.contains("*") && center != null && getWorldPlayers(center).contains(player)) {
			final String msg = event.getMessage().toLowerCase();
			for(String string : limitedcommands) {
				if(msg.startsWith(string.toLowerCase())) {
					return;
				}
			}
			if(!player.isOp()) {
				event.setCancelled(true);
				sendStringListMessage(player, null, getStringList(config, "messages.blocked command"), -1, null);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final Inventory top = player.getOpenInventory().getTopInventory();
		if(top.getHolder() == player && event.getView().getTitle().equals(lootbagInv.getTitle())) {
			event.setCancelled(true);
			player.updateInventory();
			player.closeInventory();
		}
	}
	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack i = event.getItem();
		if(i != null && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().hasLore()) {
			final Player player = event.getPlayer();
			final String playerName = player.getName();
			final ItemMeta meta = i.getItemMeta();
			if(meta.getDisplayName().equals(lootbag.getItemMeta().getDisplayName())) {
				event.setCancelled(true);
				player.updateInventory();
				removeItem(player, i, 1);
				final List<ItemStack> lootbag = getRandomLootbagContents();
				final ArrayList<String> loot = new ArrayList<>();
				int z = -1;
				for(ItemStack is : lootbag) {
					z++;
					giveItem(player, is);
					if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
						loot.add(is.getItemMeta().getDisplayName());
					} else if(is.getType().equals(Material.ENCHANTED_BOOK)) {
						loot.add(ChatColor.YELLOW + "Enchanted Book");
						final ItemStack item = lootbag.get(z);
						final ItemMeta itemMeta = item.getItemMeta();
						itemMeta.setDisplayName(loot.get(loot.size()-1));
						item.setItemMeta(itemMeta);
					}
				}

				final List<String> list = new ArrayList<>();
				for(ItemStack is : lootbag) {
					if(is != null && !is.getType().equals(Material.AIR)) {
						final ItemMeta mm = is.getItemMeta();
						final String n = mm.hasDisplayName() ? mm.getDisplayName() : toMaterial(is.getType().name(), false);
						list.add(rewardformat.replace("{NAME}", n).replace("{AMOUNT}", "" + is.getAmount()));
					}
				}

				final List<Player> online = player.getWorld().getPlayers();
				for(String string : getStringList(config, "messages.open loot bag")) {
					string = string.replace("{PLAYER}", playerName);
					if(string.equals("{REWARDS}")) {
						for(Player p : online) {
							sendStringListMessage(p, list, null);
						}
					} else {
						for(Player p : online) {
							p.sendMessage(string);
						}
					}
				}
			}
		}
	}
}