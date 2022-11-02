package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.RandomPackageAPI;
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
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public final class FileRPPlayer implements RPPlayer, UVersionableSpigot, RPStorage {
    private static final String FOLDER = DATA_FOLDER + SEPARATOR + "_Data" + SEPARATOR + "players";
    public static final HashMap<UUID, FileRPPlayer> PLAYERS = new HashMap<>();

    private boolean isLoaded;
    private final UUID uuid;
    private File file;
    private YamlConfiguration yml;

    private HashMap<String, Integer> unclaimedLootboxes;
    private List<ItemStack> unclaimedPurchases;
    private SecondaryData secondaryData;
    private CoinFlipData coinFlipData;
    private CustomEnchantData customEnchantData;
    private DisguiseData disguiseData;
    private DuelData duelData;
    private GlobalChallengeData globalChallengeData;
    private HomeData homeData;
    private ItemFilterData itemFilterData;
    private JackpotData jackpotData;
    private KitData kitData;
    private MonthlyCrateData monthlyCrateData;
    private PlayerQuestData playerQuestData;
    private RarityGemData rarityGemData;
    private ReputationData reputationData;
    private ShowcaseData showcaseData;
    private SlotBotData slotBotData;
    private TitleData titleData;

    public FileRPPlayer(@NotNull UUID uuid) {
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

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }
    @Override
    public FileRPPlayer load() {
        if(!isLoaded) {
            isLoaded = true;
        }
        return this;
    }
    @Override
    public void unload() {
        if(isLoaded) {
            isLoaded = false;
        }
    }
    @Override
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

        if(unclaimedLootboxes != null) {
            yml.set("unclaimed lootboxes", unclaimedLootboxes);
        }
        if(unclaimedPurchases != null) {
            yml.set("unclaimed purchases", null);
            for(ItemStack is : unclaimedPurchases) {
                if(is != null && !is.getType().equals(Material.AIR)) {
                    final String prefix = "unclaimed purchases." + UUID.randomUUID() + ".";
                    yml.set(prefix + "material", UMaterial.match(is).name());
                    final ItemMeta itemMeta = is.getItemMeta();
                    final String colorCharacter = "§";
                    if(itemMeta.hasDisplayName()) {
                        yml.set(prefix + "name", itemMeta.getDisplayName().replace(colorCharacter, "&"));
                    }
                    if(itemMeta.hasLore()) {
                        final List<String> lore = new ArrayList<>();
                        for(String string : itemMeta.getLore()) {
                            lore.add(string.replace(colorCharacter, "&"));
                        }
                        yml.set(prefix + "lore", lore);
                    }
                }
            }
        }
        if(secondaryData != null) {
            yml.set("secondary data.xpbottle.exhaustion expiration", secondaryData.getXPExhaustionExpiration());
        }
        if(coinFlipData != null) {
            yml.set("coinflip.notifications", coinFlipData.receivesNotifications());
            yml.set("coinflip.wins", coinFlipData.getWins());
            yml.set("coinflip.losses", coinFlipData.getLosses());
            yml.set("coinflip.wonCash", coinFlipData.getWonCash());
            yml.set("coinflip.lostCash", coinFlipData.getLostCash());
            yml.set("coinflip.taxesPaid", coinFlipData.getTaxesPaid());
        }
        if(customEnchantData != null) {
            final List<String> ids = new ArrayList<>();
            for(LivingCustomEnchantEntity cee : customEnchantData.getEntities()) {
                ids.add(cee.getEntity().getUniqueId().toString());
            }
            yml.set("custom enchants.entities", ids);
        }
        if(disguiseData != null) {
            final String active = disguiseData.getActive();
            yml.set("disguises.active", active != null ? active : "nil");
            yml.set("disguises.owned", disguiseData.getOwned());
        }
        if(duelData != null) {
            yml.set("duel.notifications", duelData.receivesNotifications());
            int i = 0;
            yml.set("duel.collection", null);
            for(ItemStack is : duelData.getCollection()) {
                yml.set("duel.collection." + i, is.toString());
                i++;
            }
            final DuelRankedData ranked = duelData.getRankedData();
            yml.set("duel.ranked.elo", ranked.getELO().doubleValue());

            final HashMap<ItemStack, List<CustomEnchantSpigot>> godset = ranked.getGodset();
            i = 0;
            for(ItemStack is : godset.keySet()) {
                final List<String> customenchants = new ArrayList<>();
                for(CustomEnchantSpigot enchant : godset.get(is)) {
                    customenchants.add(enchant.getIdentifier());
                }
                yml.set("duel.ranked.godset." + i + ".item", is.toString());
                yml.set("duel.ranked.godset." + i + ".custom enchants", customenchants);
                i++;
            }
        }
        if(globalChallengeData != null) {
            final HashMap<GlobalChallengePrize, Integer> prizes = globalChallengeData.getPrizes();
            for(GlobalChallengePrize prize : prizes.keySet()) {
                yml.set("global challenges.prizes." + prize.getPlacement(), prizes.get(prize));
            }
        }
        if(homeData != null) {
            yml.set("homes.added max homes", homeData.getAddedMaxHomes());
            final List<String> homes = new ArrayList<>();
            for(Home h : homeData.getHomes()) {
                homes.add(h.getName() + ";" + h.getIcon().name() + ";" + toString(h.getLocation()));
            }
            yml.set("homes.list", homes);
        }
        if(itemFilterData != null) {
            yml.set("item filter.enabled", itemFilterData.isActive());
            final List<String> materials = new ArrayList<>();
            for(UMaterial m : itemFilterData.getFilteredItems()) {
                materials.add(m.name());
            }
            yml.set("item filter.materials", materials);
        }
        if(jackpotData != null) {
            yml.set("jackpot.notifications", jackpotData.receivesNotifications());
            yml.set("jackpot.total tickets bought", jackpotData.getTotalTicketsBought().doubleValue());
            yml.set("jackpot.total wins", jackpotData.getTotalWins().longValue());
            yml.set("jackpot.total won cash", jackpotData.getTotalWonCash().doubleValue());
        }
        if(kitData != null) {
            final HashMap<CustomKit, Long> cooldowns = kitData.getCooldowns();
            final HashMap<CustomKit, Integer> levels = kitData.getLevels();
            for(CustomKit kit : cooldowns.keySet()) {
                yml.set("kits." + kit.getIdentifier() + ".cooldown expiration", cooldowns.get(kit));
            }
            for(CustomKit kit : levels.keySet()) {
                yml.set("kits." + kit.getIdentifier() + ".level", levels.get(kit));
            }
        }
        if(monthlyCrateData != null) {
            final HashMap<String, Boolean> owned = monthlyCrateData.getOwned();
            for(String crate : owned.keySet()) {
                yml.set("monthly crates." + crate, owned.get(crate));
            }
        }
        if(playerQuestData != null) {
            yml.set("quests", null);
            yml.set("quests.questTokens", playerQuestData.getTokens().longValue());
            final LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests = playerQuestData.getQuests();
            for(PlayerQuest quest : quests.keySet()) {
                final ActivePlayerQuest active = quests.get(quest);
                yml.set("quests." + quest.getIdentifier(), active.getStartedTime() + ";" + active.getProgress() + ";" + active.isCompleted() + ";" + active.hasClaimedRewards());
            }
        }
        if(rarityGemData != null) {
            yml.set("rarity gems", null);
            final HashMap<RarityGem, Boolean> gems = rarityGemData.getRarityGems();
            for(RarityGem gem : gems.keySet()) {
                yml.set("rarity gems." + gem.getIdentifier(), gems.get(gem));
            }
        }
        if(showcaseData != null) {
            yml.set("showcase", null);
            final HashMap<Integer, ItemStack[]> showcases = showcaseData.getShowcases();
            for(int page : showcases.keySet()) {
                yml.set("showcase." + page, null);
                yml.set("showcase." + page + ".size", showcaseData.getSize(page));
                int s = 0;
                for(ItemStack i : showcases.get(page)) {
                    if(i != null && !i.getType().equals(Material.AIR)) {
                        yml.set("showcases." + page + "." + s, i.toString());
                    }
                    s++;
                }
            }
        }
        if(slotBotData != null) {
            yml.set("slot bot.credits", slotBotData.getCredits().doubleValue());
        }
        if(titleData != null) {
            final Title active = titleData.getActive();
            final List<String> owned = new ArrayList<>();
            yml.set("titles.active", active != null ? active.getIdentifier() : "nil");
            for(Title title : titleData.getOwned()) {
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

    @Override
    @NotNull
    public UUID getUUID() {
        return uuid;
    }
    public YamlConfiguration getConfig() {
        return yml;
    }

    @NotNull
    @Override
    public HashMap<String, Integer> getUnclaimedLootboxes() {
        if(unclaimedLootboxes == null && Lootboxes.INSTANCE.isEnabled()) {
            unclaimedLootboxes = new HashMap<>();
            for(String key : getConfigurationSectionKeys(yml, "unclaimed lootboxes", false)) {
                unclaimedLootboxes.put(key, yml.getInt("unclaimed lootboxes." + key));
            }
        }
        return unclaimedLootboxes;
    }

    @NotNull
    @Override
    public List<ItemStack> getUnclaimedPurchases() {
        if(unclaimedPurchases == null && yml.get("unclaimed purchases") != null) {
            unclaimedPurchases = new ArrayList<>();
            for(String uuid : getConfigurationSectionKeys(yml, "unclaimed purchases", false)) {
                final String prefix = "unclaimed purchases." + uuid + ".";
                final UMaterial umaterial = UMaterial.match(yml.getString(prefix + "material"));
                final ItemStack is = umaterial.getItemStack();
                final ItemMeta itemMeta = is.getItemMeta();
                final String targetName = yml.getString(prefix + "name");
                if(targetName != null) {
                    itemMeta.setDisplayName(colorize(targetName));
                }
                final List<String> lore = yml.getStringList(prefix + "lore");
                if(!lore.isEmpty()) {
                    final List<String> newLore = new ArrayList<>();
                    for(String string : lore) {
                        newLore.add(colorize(string));
                    }
                    itemMeta.setLore(newLore);
                }
                is.setItemMeta(itemMeta);
                unclaimedPurchases.add(is);
            }
        }
        return unclaimedPurchases;
    }

    @NotNull
    @Override
    public SecondaryData getSecondaryData() {
        if(secondaryData == null && SecondaryEvents.INSTANCE.isEnabled()) {
            final long xpExhaustionExpiration = yml.getLong("secondary data.xpbottle.exhaustion expiration");
            secondaryData = new SecondaryDataObj(xpExhaustionExpiration);
        }
        return secondaryData;
    }

    @NotNull
    @Override
    public CoinFlipData getCoinFlipData() {
        if(coinFlipData == null && CoinFlip.INSTANCE.isEnabled()) {
            if(yml.get("coinflip") != null) {
                final BigDecimal wins = getBigDecimal("coinflip.wins"), losses = getBigDecimal("coinflip.losses"), wonCash = getBigDecimal("coinflip.wonCash"), lostCash = getBigDecimal("coinflip.lostCash"), taxesPaid = getBigDecimal("coinflip.taxesPaid");
                coinFlipData = new CoinFlipDataObj(yml.getBoolean("coinflip.notifications"), wins, losses, wonCash, lostCash, taxesPaid);
            } else if(yml.get("coinflip stats") != null) {
                final String[] values = yml.getString("coinflip stats").split(";");
                coinFlipData = new CoinFlipDataObj(Boolean.parseBoolean(yml.getString("booleans").split(";")[0]), getBigDecimal(values[0]), getBigDecimal(values[1]), getBigDecimal(values[2]), getBigDecimal(values[3]), getBigDecimal(values[4]));
            } else {
                final BigDecimal zero = BigDecimal.ZERO;
                coinFlipData = new CoinFlipDataObj(true, zero, zero, zero, zero, zero);
            }
        }
        return coinFlipData;
    }

    @NotNull
    @Override
    public CustomEnchantData getCustomEnchantData() {
        if(customEnchantData == null && CustomEnchants.getCustomEnchants().isEnabled()) {
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
            customEnchantData = new CustomEnchantDataObj(entities);
        }
        return customEnchantData;
    }

    @NotNull
    @Override
    public DisguiseData getDisguiseData() {
        if(disguiseData == null && Disguises.INSTANCE.isEnabled()) {
            String active = "";
            List<String> owned = new ArrayList<>();
            if(yml.get("disguises") != null) {
                active = yml.getString("disguises.active");
                owned = yml.getStringList("disguises.owned");
            }
            disguiseData = new DisguiseDataObj(active, owned);
        }
        return disguiseData;
    }

    @NotNull
    @Override
    public DuelData getDuelData() {
        if(duelData == null && Duels.INSTANCE.isEnabled()) {
            boolean notifications = true;
            final List<ItemStack> collection = new ArrayList<>();
            final HashMap<ItemStack, List<CustomEnchantSpigot>> godset = new HashMap<>();
            DuelRankedData ranked = null;
            if(yml.get("duel") != null) {
                notifications = yml.getBoolean("duel.notifications");
                for(String key : getConfigurationSectionKeys(yml, "duel.collection", false)) {
                    collection.add(yml.getItemStack("duel.collection." + key));
                }

                for(String key : getConfigurationSectionKeys(yml, "duel.ranked.godset", false)) {
                    final String path = "duel.ranked.godset." + key + ".";
                    final List<CustomEnchantSpigot> enchants = new ArrayList<>();
                    for(String enchant : yml.getStringList(path + "custom enchants")) {
                        final CustomEnchantSpigot ce = getCustomEnchant(enchant);
                        if(ce != null) {
                            enchants.add(ce);
                        }
                    }
                    godset.put(yml.getItemStack(path + "item"), enchants);
                }
                ranked = new DuelRankedDataObj(getBigDecimal(yml.getString("duel.ranked.elo")), godset);
            }
            duelData = new DuelDataObj(notifications, collection, ranked);
        }
        return duelData;
    }

    @NotNull
    @Override
    public GlobalChallengeData getGlobalChallengeData() {
        if(globalChallengeData == null && GlobalChallenges.getChallenges().isEnabled()) {
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
            globalChallengeData = new GlobalChallengeDataObj(prizes);
        }
        return globalChallengeData;
    }

    @NotNull
    @Override
    public HomeData getHomeData() {
        if(homeData == null && Homes.INSTANCE.isEnabled()) {
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
            homeData = new HomeDataObj(addedMaxHomes, homes);
        }
        return homeData;
    }

    @NotNull
    @Override
    public ItemFilterData getItemFilterData() {
        if(itemFilterData == null && ItemFilter.INSTANCE.isEnabled()) {
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
            itemFilterData = new ItemFilterDataObj(enabled, filter);
        }
        return itemFilterData;
    }

    @NotNull
    @Override
    public JackpotData getJackpotData() {
        if(jackpotData == null && Jackpot.INSTANCE.isEnabled()) {
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
            jackpotData = new JackpotDataObj(enabled, BigInteger.valueOf(totalTicketsBought), BigInteger.valueOf(totalWins), BigDecimal.valueOf(totalWonCash));
        }
        return jackpotData;
    }

    @NotNull
    @Override
    public KitData getKitData() {
        if(kitData == null) {
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
            kitData = new KitDataObj(cooldowns, levels);
        }
        return kitData;
    }

    @NotNull
    @Override
    public MonthlyCrateData getMonthlyCrateData() {
        if(monthlyCrateData == null && MonthlyCrates.INSTANCE.isEnabled()) {
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
            monthlyCrateData = new MonthlyCrateDataObj(owned);
        }
        return monthlyCrateData;
    }

    @NotNull
    @Override
    public PlayerQuestData getPlayerQuestData() {
        if(playerQuestData == null && PlayerQuests.getPlayerQuests().isEnabled()) {
            final LinkedHashMap<PlayerQuest, ActivePlayerQuest> activeQuests = new LinkedHashMap<>();
            BigInteger questTokens = BigInteger.ZERO;
            for(String key : getConfigurationSectionKeys(yml, "quests", false)) {
                if(key.equals("questTokens")) {
                    questTokens = BigInteger.valueOf(yml.getLong("quests." + key));
                } else {
                    final PlayerQuest quest = getPlayerQuest(key);
                    if(quest != null) {
                        final String[] values = yml.getString("quests." + key).split(";");
                        final ActivePlayerQuest active = new ActivePlayerQuest(Long.parseLong(values[0]), quest, Double.parseDouble(values[1]), Boolean.parseBoolean(values[2]), Boolean.parseBoolean(values[3]));
                        activeQuests.put(quest, active);
                    }
                }
            }
            playerQuestData = new PlayerQuestDataObj(questTokens, activeQuests);
        }
        return playerQuestData;
    }

    @NotNull
    @Override
    public RarityGemData getRarityGemData() {
        if(rarityGemData == null && RarityGems.INSTANCE.isEnabled()) {
            final HashMap<RarityGem, Boolean> gems = new HashMap<>();
            if(yml.get("rarity gems") != null) {
                for(String key : getConfigurationSectionKeys(yml, "rarity gems", false)) {
                    final RarityGem gem = getRarityGem(key);
                    if(gem != null) {
                        gems.put(gem, yml.getBoolean("rarity gems." + key));
                    }
                }
            }
            rarityGemData = new RarityGemDataObj(gems);
        }
        return rarityGemData;
    }

    @NotNull
    @Override
    public ReputationData getReputationData() {
        return reputationData;
    }

    @NotNull
    @Override
    public ShowcaseData getShowcaseData() {
        if(showcaseData == null && Showcase.INSTANCE.isEnabled()) {
            final HashMap<Integer, Integer> sizes = new HashMap<>();
            final HashMap<Integer, ItemStack[]> showcases = new HashMap<>();
            if(yml.get("showcases") != null) {
                final RandomPackageAPI api = RandomPackageAPI.INSTANCE;
                for(String pageString : getConfigurationSectionKeys(yml, "showcases", false)) {
                    final int page = Integer.parseInt(pageString);
                    sizes.put(page, yml.getInt("showcases." + pageString + ".size"));
                    final ItemStack[] items = new ItemStack[54];
                    for(String targetSlot : getConfigurationSectionKeys(yml, "showcases." + pageString, false, "size")) {
                        final int slot = Integer.parseInt(targetSlot);
                        ItemStack is;
                        try {
                            is = yml.getItemStack("showcases." + pageString + "." + targetSlot);
                        } catch (Exception e) {
                            is = api.createItemStack(yml, "showcases." + pageString + "." + targetSlot);
                        }
                        items[slot] = is;
                    }
                    showcases.put(page, items);
                }
            } else {
                final YamlConfiguration config = Showcase.INSTANCE.config;
                final int defaultShowcase = config.getInt("settings.default showcases"), defaultSize = config.getInt("settings.default showcase size");
                if(defaultShowcase > 0) {
                    final ItemStack[] a = new ItemStack[54];
                    for(int i = 1; i <= defaultShowcase; i++) {
                        showcases.put(i, a);
                        sizes.put(i, defaultSize);
                    }
                }
            }
            showcaseData = new ShowcaseDataObj(sizes, showcases);
        }
        return showcaseData;
    }

    @NotNull
    @Override
    public SlotBotData getSlotBotData() {
        if(slotBotData == null && SlotBot.INSTANCE.isEnabled()) {
            if(yml.get("slot bot") != null) {
                slotBotData = new SlotBotDataObj(getBigDecimal(yml.getString("slot bot.credits")));
            }
        }
        return slotBotData;
    }

    @NotNull
    @Override
    public TitleData getTitleData() {
        if(titleData == null && Titles.INSTANCE.isEnabled()) {
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
            titleData = new TitleDataObj(active, owned);
        }
        return titleData;
    }
}
