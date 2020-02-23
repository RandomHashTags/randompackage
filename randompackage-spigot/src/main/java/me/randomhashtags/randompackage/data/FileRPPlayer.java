package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.addon.obj.Home;
import me.randomhashtags.randompackage.api.*;
import me.randomhashtags.randompackage.api.addon.RarityGems;
import me.randomhashtags.randompackage.data.obj.*;
import me.randomhashtags.randompackage.dev.Disguises;
import me.randomhashtags.randompackage.dev.duels.Duels;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionable;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackageAPI.API;

public class FileRPPlayer implements RPPlayer, UVersionable, RPStorage {
    private static final String FOLDER = DATA_FOLDER + SEPARATOR + "_Data" + SEPARATOR + "players";
    public static final HashMap<UUID, FileRPPlayer> PLAYERS = new HashMap<>();

    private boolean isLoaded;
    private UUID uuid;
    private File file;
    private YamlConfiguration yml;

    private CoinFlipData coinflip;
    private CustomEnchantData customenchant;
    private DisguiseData disguises;
    private DuelData duel;
    private GlobalChallengeData globalchallenges;
    private HomeData home;
    private ItemFilterData itemfilter;
    private JackpotData jackpot;
    private KitData kits;
    private MonthlyCrateData monthlycrates;
    private PlayerQuestData playerquests;
    private RarityGemData raritygems;
    private ShowcaseData showcase;
    private SlotBotData slotbot;
    private TitleData titles;

    public FileRPPlayer(UUID uuid) {
        this.uuid = uuid;
        final String UUID = uuid.toString();
        final File f = new File(FOLDER, UUID + ".yml");
        boolean backup = false;
        if(!PLAYERS.containsKey(uuid)) {
            if(!f.exists()) {
                try {
                    final File folder = new File(FOLDER);
                    if(!folder.exists()) {
                        folder.mkdirs();
                    }
                    f.createNewFile();
                    backup = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            file = new File(FOLDER, UUID + ".yml");
            yml = YamlConfiguration.loadConfiguration(file);
            PLAYERS.put(uuid, this);
        }
        if(backup) {
            backup();
        }
    }

    public static FileRPPlayer get(UUID player) {
        return PLAYERS.getOrDefault(player, new FileRPPlayer(player)).load();
    }

    public boolean isLoaded() {
        return isLoaded;
    }
    public FileRPPlayer load() {
        if(!isLoaded) {
            isLoaded = true;
        }
        return this;
    }
    public void unload() {
        if(isLoaded) {
            isLoaded = false;
        }
    }
    public void backup() {

        if(yml.get("strings") != null) {
            yml.set("strings", null);
            yml.set("booleans", null);
            yml.set("ints", null);
            yml.set("longs", null);
            yml.set("coinflip stats", null);
            yml.set("custom enchant entities", null);
            yml.set("global challenge prizes", null);
            yml.set("filtered items", null);
            yml.set("homes", null);
            yml.set("owned monthly crates", null);
            yml.set("claimed monthly crates", null);
        }

        if(coinflip != null) {
            yml.set("coinflip.notifications", coinflip.receivesNotifications());
            yml.set("coinflip.wins", coinflip.getWins());
            yml.set("coinflip.losses", coinflip.getLosses());
            yml.set("coinflip.wonCash", coinflip.getWonCash());
            yml.set("coinflip.lostCash", coinflip.getLostCash());
            yml.set("coinflip.taxesPaid", coinflip.getTaxesPaid());
        }
        if(customenchant != null) {
            final List<String> ids = new ArrayList<>();
            for(LivingCustomEnchantEntity cee : customenchant.getEntities()) {
                ids.add(cee.getEntity().getUniqueId().toString());
            }
            yml.set("custom enchants.entities", ids);
        }
        if(disguises != null) {
            final String active = disguises.getActive();
            yml.set("disguises.active", active != null ? active : "nil");
            yml.set("disguises.owned", disguises.getOwned());
        }
        if(duel != null) {
            yml.set("duel.notifications", duel.receivesNotifications());
            int i = 0;
            yml.set("duel.collection", null);
            for(ItemStack is : duel.getCollection()) {
                yml.set("duel.collection." + i, is.toString());
                i++;
            }
            final DuelRankedData ranked = duel.getRankedData();
            yml.set("duel.ranked.elo", ranked.getELO().doubleValue());

            final HashMap<ItemStack, List<CustomEnchant>> godset = ranked.getGodset();
            i = 0;
            for(ItemStack is : godset.keySet()) {
                final List<String> customenchants = new ArrayList<>();
                for(CustomEnchant enchant : godset.get(is)) {
                    customenchants.add(enchant.getIdentifier());
                }
                yml.set("duel.ranked.godset." + i + ".item", is.toString());
                yml.set("duel.ranked.godset." + i + ".custom enchants", customenchants);
                i++;
            }
        }
        if(globalchallenges != null) {
            final HashMap<GlobalChallengePrize, Integer> prizes = globalchallenges.getPrizes();
            for(GlobalChallengePrize prize : prizes.keySet()) {
                yml.set("global challenges.prizes." + prize.getPlacement(), prizes.get(prize));
            }
        }
        if(home != null) {
            yml.set("homes.added max homes", home.getAddedMaxHomes());
            final List<String> homes = new ArrayList<>();
            for(Home h : home.getHomes()) {
                homes.add(h.getName() + ";" + h.getIcon().name() + ";" + toString(h.getLocation()));
            }
            yml.set("homes.list", homes);
        }
        if(itemfilter != null) {
            yml.set("item filter.enabled", itemfilter.isActive());
            final List<String> materials = new ArrayList<>();
            for(UMaterial m : itemfilter.getFilteredItems()) {
                materials.add(m.name());
            }
            yml.set("item filter.materials", materials);
        }
        if(jackpot != null) {
            yml.set("jackpot.notifications", jackpot.receivesNotifications());
            yml.set("jackpot.total tickets bought", jackpot.getTotalTicketsBought().doubleValue());
            yml.set("jackpot.total wins", jackpot.getTotalWins().longValue());
            yml.set("jackpot.total won cash", jackpot.getTotalWonCash().doubleValue());
        }
        if(kits != null) {
            final HashMap<CustomKit, Long> cooldowns = kits.getCooldowns();
            final HashMap<CustomKit, Integer> levels = kits.getLevels();
            for(CustomKit kit : cooldowns.keySet()) {
                yml.set("kits." + kit.getIdentifier() + ".cooldown expiration", cooldowns.get(kit));
            }
            for(CustomKit kit : levels.keySet()) {
                yml.set("kits." + kit.getIdentifier() + ".level", levels.get(kit));
            }
        }
        if(monthlycrates != null) {
            final HashMap<String, Boolean> owned = monthlycrates.getOwned();
            for(String crate : owned.keySet()) {
                yml.set("monthly crates." + crate, owned.get(crate));
            }
        }
        if(playerquests != null) {
            yml.set("quests", null);
            final LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests = playerquests.getQuests();
            for(PlayerQuest quest : quests.keySet()) {
                final ActivePlayerQuest active = quests.get(quest);
                yml.set("quests." + quest.getIdentifier(), active.getStartedTime() + ";" + active.getProgress() + ";" + active.isCompleted() + ";" + active.hasClaimedRewards());
            }
        }
        if(raritygems != null) {
            yml.set("rarity gems", null);
            final HashMap<RarityGem, Boolean> gems = raritygems.getRarityGems();
            for(RarityGem gem : gems.keySet()) {
                yml.set("rarity gems." + gem.getIdentifier(), gems.get(gem));
            }
        }
        if(showcase != null) {
            yml.set("showcase", null);
            final HashMap<Integer, ItemStack[]> showcases = showcase.getShowcases();
            for(int page : showcases.keySet()) {
                yml.set("showcase." + page, null);
                yml.set("showcase." + page + ".size", showcase.getSize(page));
                int s = 0;
                for(ItemStack i : showcases.get(page)) {
                    if(i != null && !i.getType().equals(Material.AIR)) {
                        yml.set("showcases." + page + "." + s, i.toString());
                    }
                    s++;
                }
            }
        }
        if(slotbot != null) {
            yml.set("slot bot.credits", slotbot.getCredits().doubleValue());
        }
        if(titles != null) {
            final Title active = titles.getActive();
            final List<String> owned = new ArrayList<>();
            yml.set("titles.active", active != null ? active.getIdentifier() : "nil");
            for(Title title : titles.getOwned()) {
                owned.add(title.getIdentifier());
            }
            yml.set("titles.owned", owned);
        }

        save();
    }

    public void save() {
        try {
            yml.save(file);
            yml = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UUID getUUID() {
        return uuid;
    }
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    public YamlConfiguration getConfig() {
        return yml;
    }

    public CoinFlipData getCoinFlipData() {
        if(coinflip == null && CoinFlip.getCoinFlip().isEnabled()) {
            if(yml.get("coinflip") != null) {
                final BigDecimal wins = getBigDecimal("coinflip.wins"), losses = getBigDecimal("coinflip.losses"), wonCash = getBigDecimal("coinflip.wonCash"), lostCash = getBigDecimal("coinflip.lostCash"), taxesPaid = getBigDecimal("coinflip.taxesPaid");
                coinflip = new CoinFlipDataObj(yml.getBoolean("coinflip.notifications"), wins, losses, wonCash, lostCash, taxesPaid);
            } else if(yml.get("coinflip stats") != null) {
                final String[] values = yml.getString("coinflip stats").split(";");
                coinflip = new CoinFlipDataObj(Boolean.parseBoolean(yml.getString("booleans").split(";")[0]), getBigDecimal(values[0]), getBigDecimal(values[1]), getBigDecimal(values[2]), getBigDecimal(values[3]), getBigDecimal(values[4]));
            } else {
                final BigDecimal zero = BigDecimal.ZERO;
                coinflip = new CoinFlipDataObj(true, zero, zero, zero, zero, zero);
            }
        }
        return coinflip;
    }

    public CustomEnchantData getCustomEnchantData() {
        if(customenchant == null && CustomEnchants.getCustomEnchants().isEnabled()) {
            List<String> list = new ArrayList<>();
            if(yml.get("custom enchants") != null) {
                list = yml.getStringList("custom enchants.entities");
            } else if(yml.get("custom enchant entities") != null) {
                list = yml.getStringList("custom enchant entities");
            }

            final List<LivingCustomEnchantEntity> entities = new ArrayList<>();
            for(String s : list) {
                final UUID uuid = UUID.fromString(s);
                final Entity entity = getEntity(uuid);
                if(entity instanceof LivingEntity && !entity.isDead()) {
                    final LivingCustomEnchantEntity e = new LivingCustomEnchantEntity(null, getPlayer(), (LivingEntity) entity, null);
                    entities.add(e);
                }
            }

            customenchant = new CustomEnchantDataObj(entities);
        }
        return customenchant;
    }

    public DisguiseData getDisguiseData() {
        if(disguises == null && Disguises.getDisguises().isEnabled()) {
            String active = "";
            List<String> owned = new ArrayList<>();
            if(yml.get("disguises") != null) {
                active = yml.getString("disguises.active");
                owned = yml.getStringList("disguises.owned");
            }
            disguises = new DisguiseDataObj(active, owned);
        }
        return disguises;
    }

    public DuelData getDuelData() {
        if(duel == null && Duels.getDuels().isEnabled()) {
            boolean notifications = true;
            final List<ItemStack> collection = new ArrayList<>();
            final HashMap<ItemStack, List<CustomEnchant>> godset = new HashMap<>();
            DuelRankedData ranked = null;
            if(yml.get("duel") != null) {
                notifications = yml.getBoolean("duel.notifications");
                for(String key : getConfigurationSectionKeys(yml, "duel.collection", false)) {
                    collection.add(yml.getItemStack("duel.collection." + key));
                }

                for(String key : getConfigurationSectionKeys(yml, "duel.ranked.godset", false)) {
                    final String path = "duel.ranked.godset." + key + ".";
                    final List<CustomEnchant> enchants = new ArrayList<>();
                    for(String enchant : yml.getStringList(path + "custom enchants")) {
                        final CustomEnchant ce = getCustomEnchant(enchant);
                        if(ce != null) {
                            enchants.add(ce);
                        }
                    }
                    godset.put(yml.getItemStack(path + "item"), enchants);
                }
                ranked = new DuelRankedDataObj(getBigDecimal(yml.getString("duel.ranked.elo")), godset);
            }
            duel = new DuelDataObj(notifications, collection, ranked);
        }
        return duel;
    }

    public GlobalChallengeData getGlobalChallengeData() {
        if(globalchallenges == null && GlobalChallenges.getChallenges().isEnabled()) {
            final HashMap<GlobalChallengePrize, Integer> prizes = new HashMap<>();
            String path = null;
            if(yml.get("global challenges") != null) {
                path = "global challenges.prizes";
            } else if(yml.get("global challenge prizes") != null) {
                path = "global challenge prizes";
            }
            if(path != null) {
                for(String key : getConfigurationSectionKeys(yml, path, false)) {
                    final GlobalChallengePrize prize = valueOfGlobalChallengePrize(Integer.parseInt(key));
                    if(prize != null) {
                        prizes.put(prize, yml.getInt(path + "." + key));
                    }
                }
            }
            globalchallenges = new GlobalChallengeDataObj(prizes);
        }
        return globalchallenges;
    }

    public HomeData getHomeData() {
        if(home == null && Homes.getHomes().isEnabled()) {
            int addedMaxHomes = 0;
            final List<Home> homes = new ArrayList<>();
            if(yml.get("homes") != null) {
                final String path;
                if(yml.get("ints") != null) {
                    addedMaxHomes = Integer.parseInt(yml.getString("ints").split(";")[2]);
                    path = "homes";
                } else {
                    addedMaxHomes = yml.getInt("homes.added max homes");
                    path = "homes.list";
                }

                for(String s : yml.getStringList(path)) {
                    final String[] values = s.split(";");
                    final String name = values[0];
                    final UMaterial mat = UMaterial.match(values[1]);
                    homes.add(new Home(name, toLocation(s.substring(name.length()+values[1].length()+2)), mat));
                }
            }
            home = new HomeDataObj(addedMaxHomes, homes);
        }
        return home;
    }

    public ItemFilterData getItemFilterData() {
        if(itemfilter == null && ItemFilter.getItemFilter().isEnabled()) {
            boolean enabled = false;
            final List<UMaterial> filter = new ArrayList<>();
            String path = null;
            if(yml.get("item filter") != null) {
                enabled = yml.getBoolean("item filter.enabled");
                path = "item filter.materials";
            } else if(yml.get("filtered items") != null) {
                enabled = Boolean.parseBoolean(yml.getString("booleans").split(";")[1]);
                path = "filtered items";
            }
            if(path != null) {
                for(String s : yml.getStringList(path)) {
                    final UMaterial umaterial = UMaterial.match(s);
                    if(umaterial != null) {
                        filter.add(umaterial);
                    }
                }
            }
            itemfilter = new ItemFilterDataObj(enabled, filter);
        }
        return itemfilter;
    }

    public JackpotData getJackpotData() {
        if(jackpot == null && Jackpot.getJackpot().isEnabled()) {
            boolean enabled = true;
            long totalTicketsBought = 0, totalWins = 0;
            double totalWonCash = 0;
            if(yml.get("jackpot") != null) {
                enabled = yml.getBoolean("jackpot.notifications");
                totalTicketsBought = yml.getLong("jackpot.total tickets bought");
                totalWins = yml.getLong("jackpot.total wins");
                totalWonCash = yml.getDouble("jackpot.total won cash");
            } else if(yml.get("booleans") != null) {
                final String[] ints = yml.getString("ints").split(";");
                enabled = Boolean.parseBoolean(yml.getString("booleans").split(";")[2]);
                totalTicketsBought = Long.parseLong(ints[0]);
                totalWins = Long.parseLong(ints[1]);
                totalWonCash = Double.parseDouble(yml.getString("longs").split(";")[0]);
            }
            jackpot = new JackpotDataObj(enabled, BigInteger.valueOf(totalTicketsBought), BigInteger.valueOf(totalWins), BigDecimal.valueOf(totalWonCash));
        }
        return jackpot;
    }

    public KitData getKitData() {
        if(kits == null) {
            final HashMap<CustomKit, Long> cooldowns = new HashMap<>();
            final HashMap<CustomKit, Integer> levels = new HashMap<>();
            if(yml.get("kits") != null) {
                for(String key : getConfigurationSectionKeys(yml, "kits", false)) {
                    final CustomKit kit = getCustomKit(key);
                    if(kit != null) {
                        final long cooldownExpiration = yml.getLong("kits." + key + ".cooldown expiration", -1);
                        final int level = yml.getInt("kits." + key + ".level", -1);
                        if(cooldownExpiration >= 0) {
                            cooldowns.put(kit, cooldownExpiration);
                        }
                        if(level >= 0) {
                            levels.put(kit, level);
                        }
                    }
                }
            }
            kits = new KitDataObj(cooldowns, levels);
        }
        return kits;
    }

    public MonthlyCrateData getMonthlyCrateData() {
        if(monthlycrates == null && MonthlyCrates.getMonthlyCrates().isEnabled()) {
            final HashMap<String, Boolean> owned = new HashMap<>();
            if(yml.get("monthly crates") != null) {
                for(String key : getConfigurationSectionKeys(yml, "monthly crates", false)) {
                    owned.put(key, yml.getBoolean("monthly crates." + key));
                }
            } else if(yml.get("owned monthly crates") != null) {
                final List<String> claimed = yml.getStringList("claimed monthly crates");
                for(String crate : yml.getStringList("owned monthly crates")) {
                    owned.put(crate, claimed.contains(crate));
                }
            }
            monthlycrates = new MonthlyCrateDataObj(owned);
        }
        return monthlycrates;
    }

    public PlayerQuestData getPlayerQuestData() {
        if(playerquests == null && PlayerQuests.getPlayerQuests().isEnabled()) {
            final LinkedHashMap<PlayerQuest, ActivePlayerQuest> activeQuests = new LinkedHashMap<>();
            if(yml.get("quests") != null) {
                for(String key : getConfigurationSectionKeys(yml, "quests", false)) {
                    final PlayerQuest quest = getPlayerQuest(key);
                    if(quest != null) {
                        final String[] values = yml.getString("quests." + key).split(";");
                        final ActivePlayerQuest active = new ActivePlayerQuest(Long.parseLong(values[0]), quest, Double.parseDouble(values[1]), Boolean.parseBoolean(values[2]), Boolean.parseBoolean(values[3]));
                        activeQuests.put(quest, active);
                    }
                }
            }
            playerquests = new PlayerQuestDataObj(activeQuests);
        }
        return playerquests;
    }

    public RarityGemData getRarityGemData() {
        if(raritygems == null && RarityGems.getRarityGems().isEnabled()) {
            final HashMap<RarityGem, Boolean> gems = new HashMap<>();
            if(yml.get("rarity gems") != null) {
                for(String key : getConfigurationSectionKeys(yml, "rarity gems", false)) {
                    final RarityGem gem = getRarityGem(key);
                    if(gem != null) {
                        gems.put(gem, yml.getBoolean("rarity gems." + key));
                    }
                }
            }
            raritygems = new RarityGemDataObj(gems);
        }
        return raritygems;
    }

    public ReputationData getReputationData() {
        return null;
    }

    public ShowcaseData getShowcaseData() {
        if(showcase == null && Showcase.getShowcase().isEnabled()) {
            final HashMap<Integer, Integer> sizes = new HashMap<>();
            final HashMap<Integer, ItemStack[]> showcases = new HashMap<>();
            if(yml.get("showcases") != null) {
                for(String pageString : yml.getConfigurationSection("showcases").getKeys(false)) {
                    final int page = Integer.parseInt(pageString);
                    sizes.put(page, yml.getInt("showcases." + pageString + ".size"));
                    final ConfigurationSection i = yml.getConfigurationSection("showcases." + pageString);
                    final ItemStack[] items = new ItemStack[54];
                    if(i != null) {
                        for(String sl : i.getKeys(false)) {
                            final int slot = Integer.parseInt(sl);
                            ItemStack is;
                            try {
                                is = yml.getItemStack("showcases." + pageString + "." + sl);
                            } catch (Exception e) {
                                is = API.createItemStack(yml, "showcases." + pageString + "." + sl);
                            }
                            items[slot] = is;
                        }
                    }
                    showcases.put(page, items);
                }
            } else {
                final YamlConfiguration config = Showcase.getShowcase().config;
                final int defaultShowcase = config.getInt("settings.default showcases"), defaultSize = config.getInt("settings.default showcase size");
                if(defaultShowcase > 0) {
                    final ItemStack[] a = new ItemStack[54];
                    for(int i = 1; i <= defaultShowcase; i++) {
                        showcases.put(i, a);
                        sizes.put(i, defaultSize);
                    }
                }
            }
            showcase = new ShowcaseDataObj(sizes, showcases);
        }
        return showcase;
    }

    public SlotBotData getSlotBotData() {
        if(slotbot == null && SlotBot.getSlotBot().isEnabled()) {
            if(yml.get("slot bot") != null) {
                slotbot = new SlotBotDataObj(getBigDecimal(yml.getString("slot bot.credits")));
            }
        }
        return slotbot;
    }

    public TitleData getTitleData() {
        if(titles == null && Titles.getTitles().isEnabled()) {
            Title active = null;
            final List<Title> owned = new ArrayList<>();
            final List<String> target = new ArrayList<>();
            if(yml.get("titles") != null) {
                active = getTitle(yml.getString("titles.active"));
                target.addAll(yml.getStringList("titles.owned"));
            } else if(yml.get("owned titles") != null) {
                active = getTitle(yml.getString("strings").split(";")[0]);
                target.addAll(yml.getStringList("owned titles"));
            }
            for(String s : target) {
                final Title title = getTitle(s);
                if(title != null) {
                    owned.add(title);
                }
            }
            titles = new TitleDataObj(active, owned);
        }
        return titles;
    }
}
