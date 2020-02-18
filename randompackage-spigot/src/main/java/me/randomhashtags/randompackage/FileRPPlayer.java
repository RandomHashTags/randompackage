package me.randomhashtags.randompackage;

import me.randomhashtags.randompackage.addon.GlobalChallengePrize;
import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.addon.Title;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.addon.obj.Home;
import me.randomhashtags.randompackage.api.*;
import me.randomhashtags.randompackage.api.addon.RarityGems;
import me.randomhashtags.randompackage.data.*;
import me.randomhashtags.randompackage.data.obj.*;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionable;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackageAPI.API;

public class FileRPPlayer implements RPPlayer, UVersionable, RPStorage {

    private boolean isLoaded;
    private UUID uuid;
    private File file;
    private YamlConfiguration yml;

    private CoinFlipData coinflip;
    private GlobalChallengeData globalchallenges;
    private HomeData home;
    private ItemFilterData itemfilter;
    private JackpotData jackpot;
    private MonthlyCrateData monthlycrates;
    private PlayerQuestData playerquests;
    private RarityGemData raritygems;
    private ShowcaseData showcase;
    private TitleData titles;

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
            yml.set("item filter.enabled", itemfilter.isEnabled());
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
        if(monthlycrates != null) {
            final HashMap<String, Boolean> owned = monthlycrates.getOwned();
            for(String key : owned.keySet()) {
                yml.set("monthly crates." + key, owned.get(key));
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
                yml.set("showcase." + page + ".size", showcase.getShowcaseSize(page));
                int s = 0;
                for(ItemStack i : showcases.get(page)) {
                    if(i != null && !i.getType().equals(Material.AIR)) {
                        yml.set("showcases." + page + "." + s, i.toString());
                    }
                    s++;
                }
            }
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

    public DisguiseData getDisguiseData() {
        return null;
    }

    public DuelData getDuelData() {
        return null;
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
        return null;
    }

    public TitleData getTitleData() {
        if(titles == null && Titles.getTitles().isEnabled()) {
            Title active = null;
            final List<Title> owned = new ArrayList<>();
            final List<String> target = new ArrayList<>();
            if(yml.get("owned titles") != null) {
                active = getTitle(yml.getString("strings").split(";")[0]);
                target.addAll(yml.getStringList("owned titles"));
            } else if(yml.get("titles") != null) {
                active = getTitle(yml.getString("titles.active"));
                target.addAll(yml.getStringList("titles.owned"));
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
