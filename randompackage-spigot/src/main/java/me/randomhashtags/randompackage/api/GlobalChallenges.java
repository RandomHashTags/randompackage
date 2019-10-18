package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.GlobalChallenge;
import me.randomhashtags.randompackage.addon.GlobalChallengePrize;
import me.randomhashtags.randompackage.addon.living.ActiveGlobalChallenge;
import me.randomhashtags.randompackage.addon.obj.GlobalChallengePrizeObject;
import me.randomhashtags.randompackage.attribute.IncreaseGlobalChallenge;
import me.randomhashtags.randompackage.event.*;
import me.randomhashtags.randompackage.event.enchant.*;
import me.randomhashtags.randompackage.util.EventAttributes;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.util.addon.FileGlobalChallenge;
import me.randomhashtags.randompackage.util.universal.UInventory;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class GlobalChallenges extends EventAttributes implements CommandExecutor {
	private static GlobalChallenges instance;
	public static GlobalChallenges getChallenges() {
		if(instance == null) instance = new GlobalChallenges();
		return instance;
	}
	
	public YamlConfiguration config;
	private MCMMOChallenges mcmmoChallenges;
	private UInventory inv, leaderboard, claimPrizes;
	private int topPlayersSize = 54;

	private File dataF;
	private YamlConfiguration data;

	public String getIdentifier() { return "GLOBAL_CHALLENGES"; }
	protected RPFeature getFeature() { return getChallenges(); }
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		if(args.length == 0 && player != null)
		    viewCurrent(player);
		else if(args.length >= 2 && args[0].equals("stop")) {
			if(hasPermission(player, "RandomPackage.globalchallenges.stop", true))
				stopChallenge(getGlobalChallenge(args[1].replace("_", " ")), false);
		} else if(player != null && args.length >= 1 && args[0].equals("claim"))
			viewPrizes(player);
		else if(args.length >= 1 && args[0].equals("reload")) {
			if(hasPermission(sender, "RandomPackage.globalchallenges.reload", true))
				reloadChallenges();
		} else if(args.length >= 3 && args[0].equals("giveprize")) {
			if(hasPermission(sender, "RandomPackage.globalchallenges.giveprize", true)) {
				final OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
				final int placing = getRemainingInt(args[2]);
				if(op != null && placing != -1) {
					RPPlayer.get(op.getUniqueId()).addGlobalChallengePrize(valueOfGlobalChallengePrize(placing));
				}
			}
		}
		return true;
	}

	public void load() {
	    final long started = System.currentTimeMillis();
	    new IncreaseGlobalChallenge().load();
		save(null, "global challenges.yml");
		save("_Data", "global challenges.yml");
		config = YamlConfiguration.loadConfiguration(new File(rpd, "global challenges.yml"));
		dataF = new File(rpd + separator + "_Data", "global challenges.yml");
		data = YamlConfiguration.loadConfiguration(dataF);

		if(!otherdata.getBoolean("saved default global challenges")) {
			final String[] c = new String[]{
					"AGGRESSIVE_MOBS_KILLED", "ALCHEMIST_EXCHANGES", "ALL_ORES_MINED",
					"BIRCH_LOGS_CUT", "BLOCKS_MINED_BY_PICKAXE", "BLOCKS_PLACED",
					"COINFLIPS_WON", "CUSTOM_ENCHANTS_REVEALED",
					"DIAMOND_ORE_MINED",
					"EMERALD_ORE_MINED", "END_MOBS_KILLED", "ENVOY_CHESTS_LOOTED", "EXP_GAINED",
					"FISH_CAUGHT",
					"GOLD_ORE_MINED",
					"JACKPOT_MONEY_SPENT", "JACKPOT_TICKETS_BOUGHT",
					"LAPIS_ORE_MINED",
					"MCMMO_XP_GAINED_IN_ACROBATICS", "MCMMO_XP_GAINED_IN_SWORDS", "MCMMO_XP_GAINED_IN_UNARMED",
                    "MOBS_KILLED",
                    "MONEY_LOST_IN_COINFLIPS", "MONEY_WON_IN_COINFLIPS",
					"PASSIVE_MOBS_KILLED", "PVP_DAMAGE",
					"RANKED_DUEL_WINS", "REDSTONE_ORE_MINED",
					"TIME_SPENT_IN_END", "TIME_SPENT_IN_MAIN_WARZONE",
					"UNIQUE_PLAYER_HEADS_COLLECTED", "UNIQUE_PLAYER_KILLS"
			};
			for(String s : c) save("global challenges", s + ".yml");
			otherdata.set("saved default global challenges", true);
			saveOtherData();
		}
		final File folder = new File(rpd + separator + "global challenges");
		if(folder.exists()) {
			for(File f : folder.listFiles()) {
				new FileGlobalChallenge(f);
			}
		}

		inv = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
		topPlayersSize = config.getInt("challenge leaderboard.how many");
		leaderboard = new UInventory(null, ((topPlayersSize+9)/9)*9, ChatColor.translateAlternateColorCodes('&', config.getString("challenge leaderboard.title")));
		claimPrizes = new UInventory(null, 9, ChatColor.translateAlternateColorCodes('&', config.getString("rewards.title")));

		for(String s : config.getConfigurationSection("rewards").getKeys(false)) {
			if(!s.equals("title")) {
				new GlobalChallengePrizeObject(d(config, "rewards." + s + ".prize"), config.getInt("rewards." + s + ".amount"), Integer.parseInt(s), config.getStringList("rewards." + s + ".prizes"));
			}
		}

		if(mcmmoIsEnabled()) {
		    mcmmoChallenges = new MCMMOChallenges();
		    pluginmanager.registerEvents(mcmmoChallenges, randompackage);
        }

		sendConsoleMessage("&6[RandomPackage] &aLoaded " + (globalchallenges != null ? globalchallenges.size() : 0) + " global challenges and " + (globalchallengeprizes != null ? globalchallengeprizes.size() : 0) + " prizes &e(took " + (System.currentTimeMillis()-started) + "ms)");
		reloadChallenges();
	}
	public void unload() {
		data.set("active global challenges", null);
		for(ActiveGlobalChallenge c : ActiveGlobalChallenge.active.values()) {
			final String p = c.getType().getIdentifier();
			data.set("active global challenges." + p + ".started", c.getStartedTime());
			final HashMap<UUID, BigDecimal> participants = c.getParticipants();
			for(UUID u : participants.keySet())
				data.set("active global challenges." + p + ".participants." + u, participants.get(u));
		}

		try {
			data.save(dataF);
			dataF = new File(rpd + separator + "_Data", "global challenges.yml");
			data = YamlConfiguration.loadConfiguration(dataF);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(mcmmoChallenges != null) {
            HandlerList.unregisterAll(mcmmoChallenges);
        }

		globalchallenges = null;
		globalchallengeprizes = null;
	}
	public void reloadChallenges() {
		final int max = config.getInt("challenge settings.max at once");
		int maxAtOnce = max;
		final ConfigurationSection EEE = data.getConfigurationSection("active global challenges");
		if(globalchallenges != null) {
			if(EEE != null) {
				final long started = System.currentTimeMillis();
				int loadeD = 0;
				for(String s : EEE.getKeys(false)) {
					if(maxAtOnce > 0) {
						loadeD += 1;
						final GlobalChallenge g = getGlobalChallenge(s);
						if(g != null) {
							final HashMap<UUID, BigDecimal> participants = new HashMap<>();
							final ConfigurationSection partic = data.getConfigurationSection("active global challenges." + s + ".participants");
							if(partic != null) {
								for(String u : partic.getKeys(false)) {
									final UUID uuid = UUID.fromString(u);
									participants.put(uuid, BigDecimal.valueOf(data.getDouble("active global challenges." + s + ".participants." + u)));
								}
							}
							maxAtOnce -= 1;
							g.start(Long.parseLong(data.getString("active global challenges." + s + ".started")), participants);
						}
					}
				}
				sendConsoleMessage("&6[RandomPackage] &aStarted " + loadeD + " pre-existing global challenges &e(took " + (System.currentTimeMillis()-started) + "ms)");
			}
			if(maxAtOnce > 0) {
				final long started = System.currentTimeMillis();
				for(int i = 1; i <= maxAtOnce; i++) {
					final GlobalChallenge r = getRandomChallenge();
					if(!r.isActive()) r.start();
					else i-=1;
				}
				sendConsoleMessage("&6[RandomPackage] &aStarted " + maxAtOnce + " new global challenges &e(took " + (System.currentTimeMillis()-started) + "ms)");
			}
		}
		reloadInventory();
	}

	public void reloadInventory() {
		final Inventory inv = this.inv.getInventory();
		int f = 0;
		for(int i = 0; i < inv.getSize(); i++) {
			if(config.get("gui." + i) != null) {
				final String p = config.getString("gui." + i + ".item");
				if(p.toUpperCase().equals("{CHALLENGE}")) {
					ActiveGlobalChallenge z = f < ActiveGlobalChallenge.active.size() ? (ActiveGlobalChallenge) ActiveGlobalChallenge.active.values().toArray()[f] : null;
					if(z == null) z = getRandomChallenge().start();
					final GlobalChallenge T = z.getType();
					final String n = T.getType();
					item = T.getItem().clone();
					itemMeta = item.getItemMeta(); lore.clear();
					for(String s : config.getStringList("challenge settings.added lore")) {
						lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("{TYPE}", n)));
					}
					itemMeta.setLore(lore); lore.clear();
					item.setItemMeta(itemMeta);
					f += 1;
				} else {
					item = d(config, "gui." + i);
				}
				inv.setItem(i, item);
			}
		}
	}


	public void viewPrizes(Player player) {
		if(hasPermission(player, "RandomPackage.globalchallenges.claim", true)) {
			final HashMap<GlobalChallengePrize, Integer> prizes = RPPlayer.get(player.getUniqueId()).getGlobalChallengePrizes();
			int size = (prizes.size()/9)*9;
			size = size == 0 ? 9 : size > 54 ? 54 : size;
			player.openInventory(Bukkit.createInventory(player, size, claimPrizes.getTitle()));
			final Inventory top = player.getOpenInventory().getTopInventory();
			player.updateInventory();
			for(GlobalChallengePrize prize : prizes.keySet()) {
				item = prize.getItem(); item.setAmount(prizes.get(prize));
				top.addItem(item);
			}
		}
	}
	public void givePrize(UUID player, GlobalChallengePrize prize, boolean sendMessage) {
		final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		if(op != null && op.isOnline()) {
			final Player p = op.getPlayer();
			final HashMap<String, ItemStack> rewards = prize.getRandomRewards();
			for(String s : rewards.keySet()) {
				giveItem(p, d(null, s));
			}
			if(sendMessage) {
				final String placing = prize.getPlacement() + "";
				for(String s : config.getStringList("messages.claimed prize"))
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{PLACING}", placing)));
			}
		}
	}
	public Map<UUID, BigDecimal> getPlacing(HashMap<UUID, BigDecimal> participants) {
		return participants.entrySet().stream().sorted(Map.Entry.<UUID, BigDecimal> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
	public Map<UUID, BigDecimal> getPlacing(HashMap<UUID, BigDecimal> participants, int returnFirst) {
		final HashMap<UUID, BigDecimal> a = new HashMap<>();
		final HashMap<UUID, BigDecimal> d = participants.entrySet().stream().sorted(Map.Entry.<UUID, BigDecimal> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		for(int i = 1; i <= returnFirst && i-1 < d.size(); i++) {
			a.put((UUID) d.keySet().toArray()[i-1], (BigDecimal) d.values().toArray()[i-1]);
		}
		return a;
	}
	public int getRanking(UUID player, ActiveGlobalChallenge g) {
		final Map<UUID, BigDecimal> byValue = g.getParticipants().entrySet().stream().sorted(Map.Entry.<UUID, BigDecimal> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		int placement = indexOf(byValue.keySet(), player);
		placement = placement == -1 ? byValue.keySet().size() : placement+1;
		return placement;
	}
	public String getRanking(int rank) {
		String ranking = formatInt(rank);
		ranking = ranking + (ranking.endsWith("1") ? "st" : ranking.endsWith("2") ? "nd" : ranking.endsWith("3") ? "rd" : ranking.equals("0") ? "" : "th");
		return ranking;
	}
	public HashMap<Integer, UUID> getRankings(ActiveGlobalChallenge g) {
		final List<UUID> participants = new ArrayList<>(g.getParticipants().keySet());
		final HashMap<Integer, UUID> rankings = new HashMap<>();
		for(UUID u : participants)
			rankings.put(getRanking(u, g), u);
		return rankings;
	}
	public void viewCurrent(Player player) {
		if(hasPermission(player, "RandomPackage.globalchallenges", true)) {
			final UUID u = player.getUniqueId();
			player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
			final Inventory top = player.getOpenInventory().getTopInventory();
			top.setContents(inv.getInventory().getContents());
			player.updateInventory();
			for(int i = 0; i < top.getSize(); i++) {
				item = top.getItem(i);
				if(item != null) {
					final ActiveGlobalChallenge g = ActiveGlobalChallenge.valueOf(item);
					if(g != null) {
						final HashMap<UUID, BigDecimal> participants = g.getParticipants();
						final Map<UUID, BigDecimal> placings = getPlacing(participants);
						int topp = 0;
						UUID ranked = topp < placings.size() ? (UUID) placings.keySet().toArray()[topp] : null;
						final String remainingtime = getRemainingTime(g.getRemainingTime());
						itemMeta = item.getItemMeta(); lore.clear();
						if(item.hasItemMeta()) {
							if(itemMeta.hasLore()) {
								final String ranking = getRanking(getRanking(u, g)), date = toReadableDate(new Date(g.getStartedTime()), config.getString("challenge settings.date format")), v = formatBigDecimal(g.getValue(u));
								for(String s : itemMeta.getLore()) {
									s = s.replace("{DATE}", date).replace("{YOUR_VALUE}", v).replace("{YOUR_RANKING}", ranking).replace("{TIME_LEFT}", remainingtime);
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
	public void stopChallenge(GlobalChallenge chall, boolean giveRewards) {
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> a = ActiveGlobalChallenge.active;
		if(a != null && a.containsKey(chall)) {
			a.get(chall).end(giveRewards, 3);
		}
	}
	public void viewTopPlayers(Player player, ActiveGlobalChallenge active) {
		player.closeInventory();
		player.openInventory(Bukkit.createInventory(player, leaderboard.getSize(), leaderboard.getTitle()));
		final Inventory top = player.getOpenInventory().getTopInventory();
		final String n = ChatColor.translateAlternateColorCodes('&', config.getString("challenge leaderboard.name")), N = active.getType().getType();
		final HashMap<Integer, UUID> rankings = getRankings(active);
		final List<String> a = config.getStringList("challenge leaderboard.lore");
		item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
		for(int i = 0; i < topPlayersSize && i < rankings.size(); i++) {
			final UUID u = rankings.get(i+1);
			final OfflinePlayer OP = Bukkit.getOfflinePlayer(u);
			final String ranking = getRanking(i+1), name = OP.getName(), value = formatBigDecimal(active.getValue(u));
			item.setAmount(i+1);
			final SkullMeta skm = (SkullMeta) item.getItemMeta();
			skm.setDisplayName(n.replace("{PLAYER}", name));
			skm.setOwner(name); lore.clear();
			for(String s : a) {
				lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("{RANKING}", ranking).replace("{CHALLENGE}", N).replace("{VALUE}", value)));
			}
			skm.setLore(lore); lore.clear();
			item.setItemMeta(skm);
			top.setItem(i, item);
		}
		player.updateInventory();
	}
	public GlobalChallenge getRandomChallenge() {
		return globalchallenges != null ? (GlobalChallenge) globalchallenges.values().toArray()[random.nextInt(globalchallenges.size())] : null;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final Inventory top = player.getOpenInventory().getTopInventory();
		if(player == top.getHolder()) {
			final String t = event.getView().getTitle();
			if(t.equals(inv.getTitle()) || t.equals(leaderboard.getTitle()) || t.equals(claimPrizes.getTitle())) {
				event.setCancelled(true);
				player.updateInventory();

				final int r = event.getRawSlot();
				final ItemStack c = event.getCurrentItem();
				
				if(r >= top.getSize() || r < 0 || !event.getClick().equals(ClickType.LEFT) && !event.getClick().equals(ClickType.RIGHT) || c == null) return;
				if(t.equals(inv.getTitle())) {
					final ActiveGlobalChallenge g = ActiveGlobalChallenge.valueOf(c);
					if(g != null) {
						player.closeInventory();
						viewTopPlayers(player, g);
					}
				} else if(t.equals(claimPrizes.getTitle())) {
					final GlobalChallengePrize prize = valueOfGlobalChallengePrize(c);
					givePrize(player.getUniqueId(), prize, true);
					item = c.clone();
					item = item.getAmount() == 1 ? new ItemStack(Material.AIR) : item;
					top.setItem(r, item);
				}
				player.updateInventory();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void entityDamageEvent(EntityDamageEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void entityDeathEvent(EntityDeathEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void blockPlaceEvent(BlockPlaceEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void blockBreakEvent(BlockBreakEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void playerFishEvent(PlayerFishEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void playerExpChangeEvent(PlayerExpGainEvent event) {
		tryIncreasing(event);
	}
	/*
	 * RandomPackage Events
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void alchemistExchangeEvent(AlchemistExchangeEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void playerRevealCustomEnchantEvent(PlayerRevealCustomEnchantEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void coinFlipEndEvent(CoinFlipEndEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void fundDepositEvent(FundDepositEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void customEnchantProcEvent(CustomEnchantProcEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void customEnchantApplyEvent(CustomEnchantApplyEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void enchanterPurchaseEvent(EnchanterPurchaseEvent event) {
		tryIncreasing(event);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void tinkererTradeEvent(TinkererTradeEvent event) {
		tryIncreasing(event);
	}
	/*
		Bukkit Events
	 */
	public void tryIncreasing(Event event) {
		for(GlobalChallenge g : ActiveGlobalChallenge.active.keySet()) {
			trigger(event, g.getAttributes());
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
			for(GlobalChallenge g : ActiveGlobalChallenge.active.keySet()) {
                trigger(event, getEntities("Player", event.getPlayer()), g.getAttributes());
			}
		}
		public void tryIncreasing(com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent event, String...replacements) {
			for(GlobalChallenge g : ActiveGlobalChallenge.active.keySet()) {
                trigger(event, getEntities("Player", event.getPlayer()), g.getAttributes(), replacements);
			}
		}
	}
}
