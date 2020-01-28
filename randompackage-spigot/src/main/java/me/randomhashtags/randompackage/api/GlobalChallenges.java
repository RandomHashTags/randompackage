package me.randomhashtags.randompackage.api;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.GlobalChallenge;
import me.randomhashtags.randompackage.addon.GlobalChallengePrize;
import me.randomhashtags.randompackage.addon.file.FileGlobalChallenge;
import me.randomhashtags.randompackage.addon.living.ActiveGlobalChallenge;
import me.randomhashtags.randompackage.addon.obj.GlobalChallengePrizeObject;
import me.randomhashtags.randompackage.attribute.IncreaseGlobalChallenge;
import me.randomhashtags.randompackage.attributesys.EACoreListener;
import me.randomhashtags.randompackage.attributesys.EventAttributeListener;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class GlobalChallenges extends EACoreListener implements CommandExecutor, EventAttributeListener {
	private static GlobalChallenges instance;
	public static GlobalChallenges getChallenges() {
		if(instance == null) instance = new GlobalChallenges();
		return instance;
	}
	
	public YamlConfiguration config;
	private MCMMOChallenges mcmmoChallenges;
	private UInventory inv, leaderboard, claimPrizes;
	private int topPlayersSize = 54, max;

	private File dataF;
	private YamlConfiguration data;

	@Override
	public String getIdentifier() {
		return "GLOBAL_CHALLENGES";
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		final int l = args.length;
		if(l == 0) {
			if(player != null) {
				viewCurrent(player);
			}
		} else {
			final String a = args[0];
			switch (a) {
				case "stop":
					if(l >= 2 && hasPermission(player, "RandomPackage.globalchallenges.stop", true)) {
						stopChallenge(getGlobalChallenge(args[1].replace("_", " ")), false);
					}
					break;
				case "claim":
					if(player != null) {
						viewPrizes(player);
					}
					break;
				case "reload":
					if(hasPermission(sender, "RandomPackage.globalchallenges.reload", true)) {
						reloadChallenges();
					}
					break;
				case "giveprize":
					if(l >= 3 && hasPermission(sender, "RandomPackage.globalchallenges.giveprize", true)) {
						final OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
						final int placing = getRemainingInt(args[2]);
						if(placing != -1) {
							RPPlayer.get(op.getUniqueId()).addGlobalChallengePrize(valueOfGlobalChallengePrize(placing));
						}
					}
					break;
				default:
					break;
			}
		}
		return true;
	}

	public void load() {
	    final long started = System.currentTimeMillis();
	    new IncreaseGlobalChallenge().load();
		registerEventAttributeListener(this);
		save("global challenges", "_settings.yml");
		save("_Data", "global challenges.yml");
		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "global challenges", "_settings.yml"));
		dataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "global challenges.yml");
		data = YamlConfiguration.loadConfiguration(dataF);

		if(!otherdata.getBoolean("saved default global challenges")) {
			generateDefaultGlobalChallenges();
			otherdata.set("saved default global challenges", true);
			saveOtherData();
		}
		for(File f : getFilesIn(DATA_FOLDER + SEPARATOR + "global challenges")) {
			if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
				new FileGlobalChallenge(f);
			}
		}

		max = config.getInt("challenge settings.max at once");
		inv = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
		topPlayersSize = config.getInt("challenge leaderboard.how many");
		leaderboard = new UInventory(null, ((topPlayersSize+9)/9)*9, colorize(config.getString("challenge leaderboard.title")));
		claimPrizes = new UInventory(null, 9, colorize(config.getString("rewards.title")));

		final List<ItemStack> list = new ArrayList<>();
		for(String s : getConfigurationSectionKeys(config, "rewards", false)) {
			if(!s.equals("title")) {
				final GlobalChallengePrizeObject obj = new GlobalChallengePrizeObject(d(config, "rewards." + s + ".prize"), config.getInt("rewards." + s + ".amount"), Integer.parseInt(s), config.getStringList("rewards." + s + ".prizes"));
				list.add(obj.getItem());
			}
		}
		addGivedpCategory(list, UMaterial.CHEST, "Global Challenge Prizes", "Givedp: GlobalChallenge Prizes");

		if(mcmmoIsEnabled()) {
		    mcmmoChallenges = new MCMMOChallenges();
		    PLUGIN_MANAGER.registerEvents(mcmmoChallenges, RANDOM_PACKAGE);
        }

		sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.GLOBAL_CHALLENGE).size() + " global challenges and " + getAll(Feature.GLOBAL_CHALLENGE_PRIZE).size() + " prizes &e(took " + (System.currentTimeMillis()-started) + "ms)");
		reloadChallenges();
	}
	public void unload() {
		data.set("active global challenges", null);
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.active;
		if(active != null) {
			for(ActiveGlobalChallenge challenge : active.values()) {
				final String id = challenge.getType().getIdentifier();
				data.set("active global challenges." + id + ".started", challenge.getStartedTime());
				final HashMap<UUID, BigDecimal> participants = challenge.getParticipants();
				for(UUID uuid : participants.keySet()) {
					data.set("active global challenges." + id + ".participants." + uuid.toString(), participants.get(uuid));
				}
			}
		}

		try {
			data.save(dataF);
			dataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "global challenges.yml");
			data = YamlConfiguration.loadConfiguration(dataF);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(mcmmoChallenges != null) {
            HandlerList.unregisterAll(mcmmoChallenges);
        }

		unregister(Feature.GLOBAL_CHALLENGE, Feature.GLOBAL_CHALLENGE_PRIZE);
		ActiveGlobalChallenge.active = null;
		unregisterEventAttributeListener(this);
	}
	public void reloadChallenges() {
		int maxAtOnce = max;
		final ConfigurationSection challengeData = data.getConfigurationSection("active global challenges");
		if(getAll(Feature.GLOBAL_CHALLENGE).size() > 0) {
			if(challengeData != null) {
				final long started = System.currentTimeMillis();
				int loaded = 0;
				for(String s : challengeData.getKeys(false)) {
					final GlobalChallenge challenge = getGlobalChallenge(s);
					if(challenge != null) {
						loaded += 1;
						final HashMap<UUID, BigDecimal> participants = new HashMap<>();
						for(String u : getConfigurationSectionKeys(data, "active global challenges." + s + ".participants", false)) {
							final UUID uuid = UUID.fromString(u);
							participants.put(uuid, BigDecimal.valueOf(data.getDouble("active global challenges." + s + ".participants." + u)));
						}
						challenge.start(Long.parseLong(data.getString("active global challenges." + s + ".started")), participants);
						maxAtOnce -= 1;
						if(maxAtOnce == 0) {
							break;
						}
					}
				}
				sendConsoleMessage("&6[RandomPackage] &aStarted " + loaded + " pre-existing global challenges &e(took " + (System.currentTimeMillis()-started) + "ms)");
			}
			if(maxAtOnce > 0) {
				final long started = System.currentTimeMillis();
				for(int i = 1; i <= maxAtOnce; i++) {
					final GlobalChallenge challenge = getRandomChallenge();
					if(!challenge.isActive()) {
						challenge.start();
					} else {
						i-=1;
					}
				}
				sendConsoleMessage("&6[RandomPackage] &aStarted " + maxAtOnce + " new global challenges &e(took " + (System.currentTimeMillis()-started) + "ms)");
			}
		}
		reloadInventory();
	}

	public void reloadInventory() {
		final Inventory inv = this.inv.getInventory();
		int f = 0;
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.active;
		final List<String> addedLore = colorizeListString(config.getStringList("challenge settings.added lore"));
		for(int i = 0; i < inv.getSize(); i++) {
			if(config.get("gui." + i) != null) {
				final String p = config.getString("gui." + i + ".item");
				if(p.toUpperCase().equals("{CHALLENGE}")) {
					ActiveGlobalChallenge targetChallenge = f < active.size() ? (ActiveGlobalChallenge) active.values().toArray()[f] : null;
					if(targetChallenge == null && f < max) {
						targetChallenge = getRandomChallenge().start();
					}
					if(targetChallenge != null) {
						final GlobalChallenge challenge = targetChallenge.getType();
						final String type = challenge.getType();
						item = challenge.getItem().clone();
						itemMeta = item.getItemMeta(); lore.clear();
						for(String s : addedLore) {
							lore.add(s.replace("{TYPE}", type));
						}
						itemMeta.setLore(lore); lore.clear();
						item.setItemMeta(itemMeta);
						f += 1;
					}
				} else {
					item = d(config, "gui." + i);
				}
				inv.setItem(i, item);
			}
		}
	}

	public void viewPrizes(@NotNull Player player) {
		if(hasPermission(player, "RandomPackage.globalchallenges.claim", true)) {
			final HashMap<GlobalChallengePrize, Integer> prizes = RPPlayer.get(player.getUniqueId()).getGlobalChallengePrizes();
			int size = (prizes.size()/9)*9;
			size = size == 0 ? 9 : Math.min(size, 54);
			player.openInventory(Bukkit.createInventory(player, size, claimPrizes.getTitle()));
			final Inventory top = player.getOpenInventory().getTopInventory();
			player.updateInventory();
			for(GlobalChallengePrize prize : prizes.keySet()) {
				item = prize.getItem();
				item.setAmount(prizes.get(prize));
				top.addItem(item);
			}
		}
	}
	public void claimPrize(@NotNull Player player, @NotNull GlobalChallengePrize prize, boolean sendMessage) {
		final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
		final HashMap<GlobalChallengePrize, Integer> prizes = pdata.getGlobalChallengePrizes();
		if(prizes.containsKey(prize)) {
			final int amount = prizes.get(prize)-1;
			if(amount <= 0) {
				prizes.remove(prize);
			} else {
				prizes.put(prize, amount);
			}
			final HashMap<String, ItemStack> rewards = prize.getRandomRewards();
			for(String s : rewards.keySet()) {
				giveItem(player, d(null, s));
			}
			if(sendMessage) {
				final String placing = prize.getPlacement() + "";
				for(String s : getStringList(config, "messages.claimed prize")) {
					player.sendMessage(s.replace("{PLACING}", placing));
				}
			}
		}
	}
	public Map<UUID, BigDecimal> getPlacing(@NotNull HashMap<UUID, BigDecimal> participants) {
		return participants.entrySet().stream().sorted(Map.Entry.<UUID, BigDecimal> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
	public Map<UUID, BigDecimal> getPlacing(@NotNull HashMap<UUID, BigDecimal> participants, int returnFirst) {
		final HashMap<UUID, BigDecimal> map = new HashMap<>();
		final HashMap<UUID, BigDecimal> d = participants.entrySet().stream().sorted(Map.Entry.<UUID, BigDecimal> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		for(int i = 1; i <= returnFirst && i-1 < d.size(); i++) {
			map.put((UUID) d.keySet().toArray()[i-1], (BigDecimal) d.values().toArray()[i-1]);
		}
		return map;
	}
	public int getRanking(@NotNull UUID player, @NotNull ActiveGlobalChallenge challenge) {
		final Map<UUID, BigDecimal> byValue = challenge.getParticipants().entrySet().stream().sorted(Map.Entry.<UUID, BigDecimal> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		int placement = indexOf(byValue.keySet(), player);
		placement = placement == -1 ? byValue.keySet().size() : placement+1;
		return placement;
	}
	public String getRanking(int rank) {
		String ranking = formatInt(rank);
		ranking = ranking + (ranking.endsWith("1") ? "st" : ranking.endsWith("2") ? "nd" : ranking.endsWith("3") ? "rd" : ranking.equals("0") ? "" : "th");
		return ranking;
	}
	public HashMap<Integer, UUID> getRankings(@NotNull ActiveGlobalChallenge challenge) {
		final List<UUID> participants = new ArrayList<>(challenge.getParticipants().keySet());
		final HashMap<Integer, UUID> rankings = new HashMap<>();
		for(UUID u : participants) {
			rankings.put(getRanking(u, challenge), u);
		}
		return rankings;
	}
	public void viewCurrent(@NotNull Player player) {
		if(hasPermission(player, "RandomPackage.globalchallenges", true)) {
			final UUID u = player.getUniqueId();
			player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
			final Inventory top = player.getOpenInventory().getTopInventory();
			top.setContents(inv.getInventory().getContents());
			player.updateInventory();
			final String dateFormat = config.getString("challenge settings.date format");
			for(int i = 0; i < top.getSize(); i++) {
				item = top.getItem(i);
				if(item != null) {
					final ActiveGlobalChallenge challenge = ActiveGlobalChallenge.valueOf(item);
					if(challenge != null) {
						final HashMap<UUID, BigDecimal> participants = challenge.getParticipants();
						final Map<UUID, BigDecimal> placings = getPlacing(participants);
						int topp = 0;
						UUID ranked = topp < placings.size() ? (UUID) placings.keySet().toArray()[topp] : null;
						final String remainingtime = getRemainingTime(challenge.getRemainingTime());
						itemMeta = item.getItemMeta(); lore.clear();
						if(item.hasItemMeta()) {
							if(itemMeta.hasLore()) {
								final String ranking = getRanking(getRanking(u, challenge)), date = toReadableDate(new Date(challenge.getStartedTime()), dateFormat), value = formatBigDecimal(challenge.getValue(u));
								for(String s : itemMeta.getLore()) {
									s = s.replace("{DATE}", date).replace("{YOUR_VALUE}", value).replace("{YOUR_RANKING}", ranking).replace("{TIME_LEFT}", remainingtime);
									if(s.contains("{TOP}")) {
										if(ranked == null) {
											s = s.replace("{TOP}", "None").replace("{VALUE}", "0");
										} else {
											s = s.replace("{TOP}", Bukkit.getOfflinePlayer(ranked).getName());
											if(s.contains("{VALUE}")) {
												final BigDecimal d = participants.getOrDefault(ranked, BigDecimal.ZERO);
												s = s.replace("{VALUE}", d.doubleValue() > 0.00 ? formatBigDecimal(d) : "0");
											}
										}
										topp += 1;
										ranked = topp < placings.size() ? (UUID) placings.keySet().toArray()[topp] : null;
									}
									lore.add(s);
								}
							}
							itemMeta.setLore(lore); lore.clear();
							item.setItemMeta(itemMeta);
						}
					}
				}
			}
			player.updateInventory();
		}
	}
	public void stopChallenge(@NotNull GlobalChallenge chall, boolean giveRewards) {
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> challenges = ActiveGlobalChallenge.active;
		if(challenges != null && challenges.containsKey(chall)) {
			challenges.get(chall).end(giveRewards, 3);
		}
	}
	public void viewTopPlayers(@NotNull Player player, @NotNull ActiveGlobalChallenge active) {
		player.closeInventory();
		player.openInventory(Bukkit.createInventory(player, leaderboard.getSize(), leaderboard.getTitle()));
		final Inventory top = player.getOpenInventory().getTopInventory();
		final String n = colorize(config.getString("challenge leaderboard.name")), challengeName = active.getType().getType();
		final HashMap<Integer, UUID> rankings = getRankings(active);
		final List<String> leaderboardLore = getStringList(config, "challenge leaderboard.lore");
		item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
		for(int i = 0; i < topPlayersSize && i < rankings.size(); i++) {
			final UUID uuid = rankings.get(i+1);
			final OfflinePlayer OP = Bukkit.getOfflinePlayer(uuid);
			final String ranking = getRanking(i+1), playerName = OP.getName(), value = formatBigDecimal(active.getValue(uuid));
			item.setAmount(i+1);
			final SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
			skullMeta.setDisplayName(n.replace("{PLAYER}", playerName));
			skullMeta.setOwner(playerName); lore.clear();
			for(String s : leaderboardLore) {
				lore.add(s.replace("{RANKING}", ranking).replace("{CHALLENGE}", challengeName).replace("{VALUE}", value));
			}
			skullMeta.setLore(lore); lore.clear();
			item.setItemMeta(skullMeta);
			top.setItem(i, item);
		}
		player.updateInventory();
	}
	public GlobalChallenge getRandomChallenge() {
		final HashMap<String, GlobalChallenge> list = getAllGlobalChallenges();
		final int size = list.size();
		return size > 0 ? (GlobalChallenge) list.values().toArray()[RANDOM.nextInt(size)] : null;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final Inventory top = player.getOpenInventory().getTopInventory();
		if(player == top.getHolder()) {
			final String title = event.getView().getTitle();
			final boolean isInventory = title.equals(inv.getTitle()), isClaimPrizes = title.equals(claimPrizes.getTitle());
			if(isInventory || title.equals(leaderboard.getTitle()) || isClaimPrizes) {
				event.setCancelled(true);
				player.updateInventory();

				final int slot = event.getRawSlot();
				final ItemStack current = event.getCurrentItem();
				if(slot >= top.getSize() || slot < 0 || !event.getClick().equals(ClickType.LEFT) && !event.getClick().equals(ClickType.RIGHT) || current == null) {
					return;
				}
				if(isInventory) {
					final ActiveGlobalChallenge challenge = ActiveGlobalChallenge.valueOf(current);
					if(challenge != null) {
						player.closeInventory();
						viewTopPlayers(player, challenge);
					}
				} else if(isClaimPrizes) {
					final GlobalChallengePrize prize = valueOfGlobalChallengePrize(current);
					claimPrize(player, prize, true);
					final int amount = current.getAmount();
					if(amount == 1) {
						top.setItem(slot, new ItemStack(Material.AIR));
					} else {
						current.setAmount(amount-1);
					}
				}
				player.updateInventory();
			}
		}
	}

	public void called(Event event) {
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.active;
		if(active != null) {
			for(GlobalChallenge g : active.keySet()) {
				trigger(event, g.getAttributes());
			}
		}
	}

	private class MCMMOChallenges implements Listener {
		@EventHandler(priority = EventPriority.HIGHEST)
		private void mcmmoAbilityActivateEvent(com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent event) {
			tryIncreasing(event);
		}
		@EventHandler(priority = EventPriority.HIGHEST)
		private void mcmmoPlayerXpGainEvent(com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent event) {
			tryIncreasing(event);
		}

		public void tryIncreasing(com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent event) {
			final HashMap<String, Entity> entities = getEntities("Player", event.getPlayer());
			final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.active;
			if(active != null) {
				for(GlobalChallenge g : active.keySet()) {
					trigger(event, entities, g.getAttributes());
				}
			}
		}
		public void tryIncreasing(com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent event) {
			final HashMap<String, Entity> entities = getEntities("Player", event.getPlayer());
			final String[] replacements = new String[] {"xp", Float.toString(event.getRawXpGained())};
			final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.active;
			if(active != null) {
				for(GlobalChallenge g : active.keySet()) {
					trigger(event, entities, g.getAttributes(), replacements);
				}
			}
		}
	}
}
