package me.randomhashtags.randompackage.api;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.event.KothCaptureEvent;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeature;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.GIVEDP_ITEM;

public class KOTH extends RPFeature implements CommandExecutor {
	private static KOTH instance;
	public static KOTH getKOTH() {
	    if(instance == null) instance = new KOTH();
	    return instance;
	}

	public YamlConfiguration config;
	private UInventory lootbagInv;
	private int scorestart, startCapCountdown = -1, scoreboard;
	
	public ItemStack lootbag;
	public DisplaySlot displaySlot;
	public String kothtitle, kothname, status;
	public Location teleportLocation, center;
	private List<String> captured, limitedcommands, lootbagrewards;
	
	public int captureTime = 0, captureRadius = 0;
	
	public long started = -1;
	
	private String rewardformat;
	private long cappingStartedTime = -1, capturedTime = -1;
	public Player currentPlayerCapturing, previouscapturer;

	public String getIdentifier() { return "KOTH"; }
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		switch (args.length) {
			case 0:
				viewStatus(sender);
				break;
			default:
				switch (args[0]) {
					case "stop":
						if(hasPermission(sender, "RandomPackage.koth.stop", true)) {
							stopKOTH();
						}
						break;
					case "start":
						if(hasPermission(sender, "RandomPackage.koth.start", true)) {
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

	public void load() {
		final long started = System.currentTimeMillis();
		save(null, "koth.yml");

		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "koth.yml"));
		lootbagInv = new UInventory(null, config.getInt("items.lootbag.size"), colorize(config.getString("items.lootbag.title")));

		lootbag = d(config, "items.lootbag");
		GIVEDP_ITEM.items.put("kothlootbag", lootbag);

		displaySlot = DisplaySlot.valueOf(config.getString("settings.scoreboards.display slot").toUpperCase());
		kothtitle = colorize(config.getString("settings.scoreboards.title"));
		kothname = colorize(config.getString("settings.name"));

		status = "Not Active";

		teleportLocation = otherdata.getString("koth.tp") != null && !otherdata.getString("koth.tp").equals("") ? toLocation(otherdata.getString("koth.tp")) : null;
		captureTime = config.getInt("settings.time to cap");
		startCapCountdown = config.getInt("settings.start cap countdown");
		captureRadius = config.getInt("settings.capture radius");
		captured = getStringList(config, "messages.captured");
		rewardformat = colorize(config.getString("messages.reward format"));
		scorestart = config.getInt("settings.scoreboards.score start");

		limitedcommands = config.getStringList("limited commands");
		lootbagrewards = config.getStringList("items.lootbag.rewards");

		final String center = otherdata.getString("koth.center");
		if(center != null && !center.equals("")) {
			this.center = toLocation(center);
			startKOTH();
		}

		sendConsoleMessage("&6[RandomPackage] &aLoaded King of the Hill &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		otherdata.set("koth.center", center != null ? toString(center) : "");
		otherdata.set("koth.tp", teleportLocation != null ? toString(teleportLocation) : "");
		saveOtherData();

		if(center != null && !status.equals("STOPPED")) {
			for(Player player : center.getWorld().getPlayers()) {
				player.setScoreboard(SCOREBOARD_MANAGER.getNewScoreboard());
			}
		}
		stopKOTH();
	}
	
	public ArrayList<ItemStack> getRandomLootbagContents() {
		final ArrayList<ItemStack> L = new ArrayList<>();
		for(String s : lootbagrewards) {
			final String S = s.toLowerCase();
			final boolean c = S.contains(";chance"), C = s.startsWith("chance=");
			final int i = C ? Integer.parseInt(S.split("chance=")[1].split("-")[0]) : c ? Integer.parseInt(S.split(";chance=")[1]) : 100;
			if(RANDOM.nextInt(100) < i) {
				final String original = C ? s.split("chance=" + i + "->")[1] : c ? s.split(";chance=")[0] : s;
				String r = C ? S.split("chance=" + i + "->")[1] : c ? S.split(";chance=")[0] : s;
				if(r.contains("||")) {
					final String[] a = r.split("\\|\\|");
					r = original.split("\\|\\|")[RANDOM.nextInt(a.length)];
				}
				final ItemStack is = d(null, r);
				if(is != null && !is.getType().equals(Material.AIR)) L.add(is);
			}
		}
		return L;
	}
	public void previewLootbag(Player player) {
		if(hasPermission(player, "RandomPackage.koth.loot", true)) {
			player.openInventory(Bukkit.createInventory(player, lootbagInv.getSize(), lootbagInv.getTitle()));
			final Inventory top = player.getOpenInventory().getTopInventory();
			final ArrayList<ItemStack> lootbag = getRandomLootbagContents();
			final String n = player.getName();
			for(ItemStack is : lootbag) {
			    final int f = top.firstEmpty();
				if(is != null && !(f < 0)) {
				    item = is;
				    itemMeta = item.getItemMeta();
				    if(itemMeta != null && itemMeta.hasLore()) {
				        lore.clear();
				        for(String s : itemMeta.getLore()) {
				            lore.add(s.replace("{PLAYER}", n).replace("{UNLOCKED_BY}", n));
                        }
				        itemMeta.setLore(lore); lore.clear();
				        item.setItemMeta(itemMeta);
                    }
					top.setItem(f, item);
				}
			}
			player.updateInventory();
		}
	}
	public long getTimeLeft() {
		final int i = captureTime*1000;
		return cappingStartedTime > 0 ? cappingStartedTime+i-System.currentTimeMillis() : i;
	}
	public long getRuntime() {
		return System.currentTimeMillis()-started;
	}
	public void viewStatus(CommandSender sender) {
		if(hasPermission(sender, "RandomPackage.koth", true)) {
			final boolean captured = status.equals("CAPTURED"), stopped = status.equals("STOPPED");
			final String status = config.getString("messages.status." + (center == null || captured || stopped ? "closed" : "open")),
					name = currentPlayerCapturing != null ? currentPlayerCapturing.getName() : "",
					flag = config.getString("messages.flag." + (captured || stopped ? "captured" : currentPlayerCapturing == null ? "uncontested" : "capturing"));

			String faction = currentPlayerCapturing != null ? regions.getFactionTag(currentPlayerCapturing.getUniqueId()) : "";
			if(!faction.equals("")) faction = faction + " ";

			final String join = config.getString("messages.join event"), timeleft = getRemainingTime(getTimeLeft()), runtime = getRemainingTime(getRuntime());
			final String players = center != null ? formatInt(center.getWorld().getNearbyEntities(center, captureRadius, captureRadius, captureRadius).size()) : "0";

			for(String string : getStringList(config, "messages.command")) {
				String i = string;
				if(i.contains("{STATUS}"))
					i = i.replace("{STATUS}", center != null ? status : "&c&lNOT SETUP");
				if(i.contains("{PLAYERS}"))
					i = center != null ? i.replace("{PLAYERS}", players) : null;
				if(i != null) {
					if(captured) {
						if(i.contains("{CAPTURED_BY}")) i = i.replace("{CAPTURED_BY}", name);
						if(i.contains("{NEXT_KOTH_TIME}")) i = i.replace("{NEXT_KOTH_TIME}", "&f&lUnspecified");
						if(i.contains("{RUNTIME}") || i.contains("{JOIN_EVENT}") || i.contains("{TIME_LEFT}"))     i = null;

					} else if(stopped) {
						if(i.contains("{NEXT_KOTH_TIME}")) i = i.replace("{NEXT_KOTH_TIME}", "");
						if(i.contains("{RUNTIME}") || i.contains("JOIN_EVENT") || i.contains("{FLAG}") || i.contains("{FACTION}") || i.contains("{TIME_LEFT}")
								|| i.contains("{CAPTURED_BY}") || i.contains("{PLAYERS}"))
							i = null;

					} else if(this.status.equals("ACTIVE")) {
						if(i.contains("{RUNTIME}"))   i = i.replace("{RUNTIME}", runtime);
						if(i.contains("{JOIN_EVENT}"))i = i.replace("{JOIN_EVENT}", join);
						if(i.contains("{PLAYER}"))    i = i.replace("{PLAYER}", name);
						if(i.contains("{FACTION}"))   i = i.replace("{FACTION}", faction);
						if(i.contains("{TIME_LEFT}")) i = i.replace("{TIME_LEFT}", timeleft);
						if(i.contains("{FLAG}"))      i = i.replace("{FLAG}", flag);
						if(i.contains("{NEXT_KOTH_TIME}") || i.contains("{CAPTURED_BY}"))  i = null;
					} else {
						if(i.contains("{RUNTIME}") || i .contains("{JOIN_EVENT}") || i.contains("{PLAYER}") || i.contains("FACTION")
								|| i.contains("{TIME_LEFT}") || i.contains("{FLAG}") || i.contains("{NEXT_KOTH_TIME}") || i.contains("{CAPTURED_BY}"))
							i = null;
					}
					if(i != null) sender.sendMessage(colorize(i));
				}
			}
		}
	}
	public void setCenter(CommandSender sender, Location l) {
		if(hasPermission(sender, "RandomPackage.koth.setcenter", true)) {
			center = l;
			final HashMap<String, String> replacements = new HashMap<>();
			replacements.put("{LOCATION}", l.getBlockX() + "x, " + l.getBlockY() + "y, " + l.getBlockZ());
			sendStringListMessage(sender, getStringList(config, "messages.set center"), null);
		}
	}
	
	public void startKOTH() {
		started = System.currentTimeMillis();
		status = "ACTIVE";
		if(center != null) {
			scoreboard = SCHEDULER.scheduleSyncRepeatingTask(RANDOM_PACKAGE, () -> {
				final boolean c = status.equals("CAPTURED");
				setScoreboard(c);
				if(c) SCHEDULER.cancelTask(scoreboard);
			}, 0, 20);
		}
	}
	public void stopKOTH() {
		status = "STOPPED";
		SCHEDULER.cancelTask(scoreboard);
		currentPlayerCapturing = null;
		cappingStartedTime = -1;
		capturedTime = -1;
		if(center != null)
			for(Player player : center.getWorld().getPlayers())
				player.setScoreboard(SCOREBOARD_MANAGER.getNewScoreboard());
	}

	public void broadcastStartCapping() {
		final String time = getRemainingTime(getTimeLeft()), c = currentPlayerCapturing.getName();
		final List<String> m = colorizeListString(getStringList(config, "messages.start capping"));
		final HashMap<String, String> replacements = new HashMap<>();
		replacements.put("{PLAYER}", c);
		replacements.put("{TIME}", time);
		for(Player player : center.getWorld().getPlayers()) {
			sendStringListMessage(player, m, replacements);
		}
	}
	public void broadcastCapping() {
		final String t = getRemainingTime(getTimeLeft());
		final List<String> m = getStringList(config, "messages.capping");
		for(Player player : center.getWorld().getPlayers()) {
			for(String string : m) {
				if(string.contains("{PLAYER}")) string = string.replace("{PLAYER}", currentPlayerCapturing.getName());
				if(string.contains("{TIME}")) string = string.replace("{TIME}", t);
				player.sendMessage(colorize(string));
			}
		}
	}
	public void broadcastNoLongerCapping() {
		final List<String> m = colorizeListString(getStringList(config, "messages.no longer capping"));
		final String p = previouscapturer.getName();
		for(Player player : center.getWorld().getPlayers()) {
			for(String string : m) {
				if(string.contains("{PLAYER}")) string = string.replace("{PLAYER}", p);
				player.sendMessage(string);
			}
		}
	}

	private void setScoreboard(boolean captured) {
		if(center == null) return;
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

		final Collection<Player> kothPlayers = center.getWorld().getPlayers();
		String closestPlayerToKOTH = null;
		int distance = captureRadius;

		if(!isStopped) {
			if(!isCaptured && isCapturing) {
				if(captureTimeLeft <= 0) {
					final KothCaptureEvent e = new KothCaptureEvent(currentPlayerCapturing);
					PLUGIN_MANAGER.callEvent(e);
					if(!e.isCancelled()) {
						final String currentCapturer = currentPlayerCapturing.getName();
						status = "CAPTURED";
						for(Player player : kothPlayers) {
							for(String string : this.captured) {
								player.sendMessage(string.replace("{PLAYER}", currentCapturer));
							}
						}
						item = lootbag.clone(); itemMeta = item.getItemMeta(); lore.clear();
						for(String string : itemMeta.getLore()) {
							lore.add(string.replace("{PLAYER}", currentCapturer));
						}
						itemMeta.setLore(lore); lore.clear();
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
		if(hasPermission(player, "RandomPackage.koth.teleport", true) && teleportLocation != null) {
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
		final World w = center != null ? center.getWorld() : null;
		if(w != null) {
			final Player player = event.getPlayer();
			final boolean stopped = status.equals("STOPPED");
			if(event.getTo().getWorld().equals(w) && event.getCause().equals(TeleportCause.COMMAND) && !player.hasPermission("RandomPackage.koth.teleport bypass") && (stopped || status.equals("CAPTURED"))) {
				event.setCancelled(true);
				sendStringListMessage(player, null, getStringList(config, "messages." + (stopped ? "no event running" : "already capped")), -1, null);
			} else if(event.getFrom().getWorld().equals(w)) {
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
		if(!status.equals("STOPPED") && !limitedcommands.contains("*") && center != null && center.getWorld().getPlayers().contains(player)) {
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
			final ItemMeta m = i.getItemMeta();
			if(m.getDisplayName().equals(lootbag.getItemMeta().getDisplayName())) {
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
						item = lootbag.get(z); itemMeta = item.getItemMeta();
						itemMeta.setDisplayName(loot.get(loot.size() - 1));
						item.setItemMeta(itemMeta);
					}
				}

				final ArrayList<String> a = new ArrayList<>();
				for(ItemStack is : lootbag) {
					if(is != null && !is.getType().equals(Material.AIR)) {
						final ItemMeta mm = is.getItemMeta();
						final String n = mm.hasDisplayName() ? mm.getDisplayName() : toMaterial(is.getType().name(), false);
						a.add(rewardformat.replace("{NAME}", n).replace("{AMOUNT}", "" + is.getAmount()));
					}
				}

				final List<Player> online = player.getWorld().getPlayers();
				for(String string : getStringList(config, "messages.open loot bag")) {
					string = string.replace("{PLAYER}", playerName);
					if(string.equals("{REWARDS}")) {
						for(Player p : online) {
							sendStringListMessage(p, a, null);
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