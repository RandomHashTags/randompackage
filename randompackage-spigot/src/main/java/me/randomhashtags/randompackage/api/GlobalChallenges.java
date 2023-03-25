package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.GlobalChallenge;
import me.randomhashtags.randompackage.addon.GlobalChallengePrize;
import me.randomhashtags.randompackage.addon.file.FileGlobalChallenge;
import me.randomhashtags.randompackage.addon.living.ActiveGlobalChallenge;
import me.randomhashtags.randompackage.addon.obj.GlobalChallengePrizeObject;
import me.randomhashtags.randompackage.attribute.IncreaseGlobalChallenge;
import me.randomhashtags.randompackage.attributesys.EventAttributeListener;
import me.randomhashtags.randompackage.attributesys.EventExecutor;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.RPPlayer;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.perms.GlobalChallengePermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public enum GlobalChallenges implements RPFeatureSpigot, EventExecutor, CommandExecutor, EventAttributeListener {
	INSTANCE;
	
	public YamlConfiguration config;
	private MCMMOChallenges mcmmo_challenges;
	private UInventory inv, leaderboard, claim_prizes;
	private int top_players_size = 54, max;

	private File dataF;
	private YamlConfiguration data;

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
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
					if(l >= 2 && hasPermission(player, GlobalChallengePermission.COMMAND_STOP_CHALLENGE, true)) {
						stop_challenge(getGlobalChallenge(args[1].replace("_", " ")), false);
					}
					break;
				case "claim":
					if(player != null) {
						view_prizes(player);
					}
					break;
				case "reload":
					if(hasPermission(sender, GlobalChallengePermission.COMMAND_RELOAD, true)) {
						reloadChallenges();
					}
					break;
				case "giveprize":
					if(l >= 3 && hasPermission(sender, GlobalChallengePermission.COMMAND_GIVE_PRIZE, true)) {
						final OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
						final int placing = getRemainingInt(args[2]);
						if(placing != -1) {
							final GlobalChallengePrize prize = valueOfGlobalChallengePrize(placing);
							if(prize != null) {
								FileRPPlayer.get(op.getUniqueId()).getGlobalChallengeData().addPrize(prize);
							}
						}
					}
					break;
				default:
					break;
			}
		}
		return true;
	}

	@Override
	public void load() {
	    final long started = System.currentTimeMillis();
	    new IncreaseGlobalChallenge().load();

		save("global challenges", "_settings.yml");
		save("_Data", "global challenges.yml");
		config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "global challenges", "_settings.yml"));
		dataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "global challenges.yml");
		data = YamlConfiguration.loadConfiguration(dataF);

		if(!OTHER_YML.getBoolean("saved default global challenges")) {
			generateDefaultGlobalChallenges();
			OTHER_YML.set("saved default global challenges", true);
			saveOtherData();
		}
		for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "global challenges")) {
			if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
				new FileGlobalChallenge(f);
			}
		}

		max = config.getInt("challenge settings.max at once");
		inv = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
		top_players_size = config.getInt("challenge leaderboard.how many");
		leaderboard = new UInventory(null, ((top_players_size +9)/9)*9, colorize(config.getString("challenge leaderboard.title")));
		claim_prizes = new UInventory(null, 9, colorize(config.getString("rewards.title")));

		final List<ItemStack> list = new ArrayList<>();
		for(String s : getConfigurationSectionKeys(config, "rewards", false)) {
			if(!s.equals("title")) {
				final GlobalChallengePrizeObject obj = new GlobalChallengePrizeObject(createItemStack(config, "rewards." + s + ".prize"), config.getInt("rewards." + s + ".amount"), Integer.parseInt(s), config.getStringList("rewards." + s + ".prizes"));
				list.add(obj.getItem());
			}
		}
		addGivedpCategory(list, UMaterial.CHEST, "Global Challenge Prizes", "Givedp: GlobalChallenge Prizes");

		if(RPFeatureSpigot.mcmmoIsEnabled()) {
		    mcmmo_challenges = new MCMMOChallenges();
		    PLUGIN_MANAGER.registerEvents(mcmmo_challenges, RANDOM_PACKAGE);
        }

		sendConsoleDidLoadFeature(getAll(Feature.GLOBAL_CHALLENGE).size() + " global challenges and " + getAll(Feature.GLOBAL_CHALLENGE_PRIZE).size() + " prizes", started);
		reloadChallenges();
	}
	@Override
	public void unload() {
		data.set("active global challenges", null);
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.ACTIVE;
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

		save();
		if(mcmmo_challenges != null) {
            HandlerList.unregisterAll(mcmmo_challenges);
        }
		unregister(Feature.GLOBAL_CHALLENGE, Feature.GLOBAL_CHALLENGE_PRIZE);
		ActiveGlobalChallenge.ACTIVE = null;
	}

	public void save() {
		try {
			data.save(dataF);
			dataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "global challenges.yml");
			data = YamlConfiguration.loadConfiguration(dataF);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void reloadChallenges() {
		int maxAtOnce = max;
		final ConfigurationSection challengeData = data.getConfigurationSection("active global challenges");
		if(getAll(Feature.GLOBAL_CHALLENGE).size() > 0) {
			if(challengeData != null) {
				final long started = System.currentTimeMillis();
				int loaded = 0;
				for(String string : challengeData.getKeys(false)) {
					final GlobalChallenge challenge = getGlobalChallenge(string);
					if(challenge != null) {
						loaded += 1;
						final HashMap<UUID, BigDecimal> participants = new HashMap<>();
						for(String u : getConfigurationSectionKeys(data, "active global challenges." + string + ".participants", false)) {
							final UUID uuid = UUID.fromString(u);
							participants.put(uuid, BigDecimal.valueOf(data.getDouble("active global challenges." + string + ".participants." + u)));
						}
						challenge.start(Long.parseLong(data.getString("active global challenges." + string + ".started")), participants);
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
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.ACTIVE;
		final List<String> addedLore = colorizeListString(config.getStringList("challenge settings.added lore"));
		for(int i = 0; i < inv.getSize(); i++) {
			if(config.get("gui." + i) != null) {
				final String targetItem = config.getString("gui." + i + ".item");
				ItemStack item = null;
				if(targetItem != null && targetItem.equalsIgnoreCase("{CHALLENGE}")) {
					ActiveGlobalChallenge targetChallenge = f < active.size() ? (ActiveGlobalChallenge) active.values().toArray()[f] : null;
					if(targetChallenge == null && f < max) {
						targetChallenge = getRandomChallenge().start();
					}
					if(targetChallenge != null) {
						final GlobalChallenge challenge = targetChallenge.getType();
						final String type = challenge.getType();
						item = challenge.getItem().clone();
						final ItemMeta itemMeta = item.getItemMeta();
						final List<String> lore = new ArrayList<>();
						for(String string : addedLore) {
							lore.add(string.replace("{TYPE}", type));
						}
						itemMeta.setLore(lore); lore.clear();
						item.setItemMeta(itemMeta);
						f += 1;
					}
				} else {
					item = createItemStack(config, "gui." + i);
				}
				inv.setItem(i, item);
			}
		}
	}

	public void view_prizes(@NotNull Player player) {
		if(hasPermission(player, GlobalChallengePermission.VIEW_PRIZES, true)) {
			final HashMap<GlobalChallengePrize, Integer> prizes = FileRPPlayer.get(player.getUniqueId()).getGlobalChallengeData().getPrizes();
			int size = (prizes.size()/9)*9;
			size = size == 0 ? 9 : Math.min(size, 54);
			player.openInventory(Bukkit.createInventory(player, size, claim_prizes.getTitle()));
			final Inventory top = player.getOpenInventory().getTopInventory();
			player.updateInventory();
			for(GlobalChallengePrize prize : prizes.keySet()) {
				final ItemStack item = prize.getItem();
				item.setAmount(prizes.get(prize));
				top.addItem(item);
			}
		}
	}
	public void claim_prize(@NotNull Player player, @NotNull GlobalChallengePrize prize, boolean sendMessage) {
		final RPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
		final HashMap<GlobalChallengePrize, Integer> prizes = pdata.getGlobalChallengeData().getPrizes();
		if(prizes.containsKey(prize)) {
			final int amount = prizes.get(prize) - 1;
			if(amount <= 0) {
				prizes.remove(prize);
			} else {
				prizes.put(prize, amount);
			}
			final HashMap<String, ItemStack> rewards = prize.getRandomRewards();
			for(String reward : rewards.keySet()) {
				giveItem(player, createItemStack(null, reward));
			}
			if(sendMessage) {
				final String placing = prize.getPlacement() + "";
				for(String string : getStringList(config, "messages.claimed prize")) {
					player.sendMessage(string.replace("{PLACING}", placing));
				}
			}
		}
	}
	@NotNull
	public Map<UUID, BigDecimal> getPlacing(@NotNull HashMap<UUID, BigDecimal> participants) {
		return participants.entrySet().stream().sorted(Map.Entry.<UUID, BigDecimal> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
	@NotNull
	public Map<UUID, BigDecimal> getPlacing(@NotNull HashMap<UUID, BigDecimal> participants, int returnFirst) {
		final HashMap<UUID, BigDecimal> map = new HashMap<>();
		final HashMap<UUID, BigDecimal> d = participants.entrySet().stream().sorted(Map.Entry.<UUID, BigDecimal> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		final Object[] keys = d.keySet().toArray(), values = d.values().toArray();
		for(int i = 1; i <= returnFirst && i-1 < d.size(); i++) {
			map.put((UUID) keys[i-1], (BigDecimal) values[i-1]);
		}
		return map;
	}
	public int getRanking(@NotNull UUID player, @NotNull ActiveGlobalChallenge challenge) {
		final Map<UUID, BigDecimal> byValue = challenge.getParticipants().entrySet().stream().sorted(Map.Entry.<UUID, BigDecimal> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		final Set<UUID> keys = byValue.keySet();
		int placement = indexOf(keys, player);
		placement = placement == -1 ? keys.size() : placement+1;
		return placement;
	}
	@NotNull
	public String getRanking(int rank) {
		String ranking = formatInt(rank);
		ranking = ranking + (ranking.endsWith("1") ? "st" : ranking.endsWith("2") ? "nd" : ranking.endsWith("3") ? "rd" : ranking.equals("0") ? "" : "th");
		return ranking;
	}
	@NotNull
	public HashMap<Integer, UUID> getRankings(@NotNull ActiveGlobalChallenge challenge) {
		final Set<UUID> participants = challenge.getParticipants().keySet();
		final HashMap<Integer, UUID> rankings = new HashMap<>();
		for(UUID u : participants) {
			rankings.put(getRanking(u, challenge), u);
		}
		return rankings;
	}
	public void viewCurrent(@NotNull Player player) {
		if(hasPermission(player, GlobalChallengePermission.VIEW, true)) {
			final UUID playerUUID = player.getUniqueId();
			player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
			final Inventory top = player.getOpenInventory().getTopInventory();
			top.setContents(inv.getInventory().getContents());
			player.updateInventory();
			final String dateFormat = config.getString("challenge settings.date format");
			for(int i = 0; i < top.getSize(); i++) {
				final ItemStack item = top.getItem(i);
				if(item != null) {
					final ActiveGlobalChallenge challenge = ActiveGlobalChallenge.valueOf(item);
					if(challenge != null) {
						final HashMap<UUID, BigDecimal> participants = challenge.getParticipants();
						final Map<UUID, BigDecimal> placings = getPlacing(participants);
						int topp = 0;
						UUID ranked = topp < placings.size() ? (UUID) placings.keySet().toArray()[topp] : null;
						final String remainingtime = getRemainingTime(challenge.getRemainingTime());
						final ItemMeta itemMeta = item.getItemMeta();
						final List<String> lore = new ArrayList<>();
						if(itemMeta != null) {
							if(itemMeta.hasLore()) {
								final String ranking = getRanking(getRanking(playerUUID, challenge)), date = toReadableDate(new Date(challenge.getStartedTime()), dateFormat), value = formatBigDecimal(challenge.getValue(playerUUID));
								for(String string : itemMeta.getLore()) {
									string = string.replace("{DATE}", date).replace("{YOUR_VALUE}", value).replace("{YOUR_RANKING}", ranking).replace("{TIME_LEFT}", remainingtime);
									if(string.contains("{TOP}")) {
										if(ranked == null) {
											string = string.replace("{TOP}", "None").replace("{VALUE}", "0");
										} else {
											string = string.replace("{TOP}", Bukkit.getOfflinePlayer(ranked).getName());
											if(string.contains("{VALUE}")) {
												final BigDecimal d = participants.getOrDefault(ranked, BigDecimal.ZERO);
												string = string.replace("{VALUE}", d.doubleValue() > 0.00 ? formatBigDecimal(d) : "0");
											}
										}
										topp += 1;
										ranked = topp < placings.size() ? (UUID) placings.keySet().toArray()[topp] : null;
									}
									lore.add(string);
								}
							}
							itemMeta.setLore(lore);
							lore.clear();
							item.setItemMeta(itemMeta);
						}
					}
				}
			}
			player.updateInventory();
		}
	}
	public void stop_challenge(@NotNull GlobalChallenge challenge, boolean giveRewards) {
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> challenges = ActiveGlobalChallenge.ACTIVE;
		if(challenges != null && challenges.containsKey(challenge)) {
			challenges.get(challenge).end(giveRewards, 3);
		}
	}
	public void viewTopPlayers(@NotNull Player player, @NotNull ActiveGlobalChallenge active) {
		player.closeInventory();
		player.openInventory(Bukkit.createInventory(player, leaderboard.getSize(), leaderboard.getTitle()));
		final Inventory top = player.getOpenInventory().getTopInventory();
		final String leaderboardName = colorize(config.getString("challenge leaderboard.name")), challengeName = active.getType().getType();
		final HashMap<Integer, UUID> rankings = getRankings(active);
		final List<String> leaderboardLore = getStringList(config, "challenge leaderboard.lore");
		final ItemStack item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
		for(int i = 0; i < top_players_size && i < rankings.size(); i++) {
			final UUID uuid = rankings.get(i+1);
			final OfflinePlayer OP = Bukkit.getOfflinePlayer(uuid);
			final String ranking = getRanking(i+1), playerName = OP.getName(), value = formatBigDecimal(active.getValue(uuid));
			item.setAmount(i+1);
			final SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
			skullMeta.setDisplayName(leaderboardName.replace("{PLAYER}", playerName));
			skullMeta.setOwner(playerName);
			final List<String> lore = new ArrayList<>();
			for(String s : leaderboardLore) {
				lore.add(s.replace("{RANKING}", ranking).replace("{CHALLENGE}", challengeName).replace("{VALUE}", value));
			}
			skullMeta.setLore(lore);
			item.setItemMeta(skullMeta);
			top.setItem(i, item);
		}
		player.updateInventory();
	}
	@Nullable
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
			final boolean isInventory = title.equals(inv.getTitle()), isClaimPrizes = title.equals(claim_prizes.getTitle());
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
					if(prize != null) {
						claim_prize(player, prize, true);
					}
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

	public void called(@NotNull Event event) {
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.ACTIVE;
		if(active != null) {
			for(GlobalChallenge challenge : active.keySet()) {
				trigger(event, challenge.getAttributes());
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
			final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.ACTIVE;
			if(active != null) {
				for(GlobalChallenge g : active.keySet()) {
					trigger(event, entities, g.getAttributes());
				}
			}
		}
		public void tryIncreasing(com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent event) {
			final HashMap<String, Entity> entities = getEntities("Player", event.getPlayer());
			final String[] replacements = new String[] {"xp", Float.toString(event.getRawXpGained())};
			final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.ACTIVE;
			if(active != null) {
				for(GlobalChallenge g : active.keySet()) {
					trigger(event, entities, g.getAttributes(), replacements);
				}
			}
		}
	}
}
