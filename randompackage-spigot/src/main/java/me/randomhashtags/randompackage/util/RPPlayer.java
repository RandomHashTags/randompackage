package me.randomhashtags.randompackage.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.addon.stats.CoinFlipStats;
import me.randomhashtags.randompackage.addon.obj.Home;
import me.randomhashtags.randompackage.addon.stats.DuelStats;
import me.randomhashtags.randompackage.api.Homes;
import me.randomhashtags.randompackage.api.PlayerQuests;
import me.randomhashtags.randompackage.api.Showcase;
import me.randomhashtags.randompackage.addon.PlayerRank;
import me.randomhashtags.randompackage.dev.RPStorage;
import me.randomhashtags.randompackage.event.PlayerQuestExpireEvent;
import me.randomhashtags.randompackage.event.PlayerQuestStartEvent;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;
import static me.randomhashtags.randompackage.RandomPackageAPI.api;
import static me.randomhashtags.randompackage.supported.economy.Vault.permissions;

public class RPPlayer extends RegionalAPI implements RPStorage {
    private static final String folder = dataFolder + separator + "_Data" + separator + "players";
    public static final HashMap<UUID, RPPlayer> players = new HashMap<>();
    private static final HashMap<UUID, List<Integer>> questTasks = new HashMap<>();

    public UUID uuid;
    public File file = null;
    public YamlConfiguration yml = null;

    private PlayerRank rank;
    private CoinFlipStats coinflipStats;
    private DuelStats duelStats;
    private Title activeTitle;
    public BigDecimal jackpotWonCash = BigDecimal.ZERO, jackpotTickets = BigDecimal.ZERO;
    public long xpExhaustionExpiration = 0;
    public int jackpotWins, addedMaxHomes , questTokens , reputationPoints;
    private boolean coinflipNotifications = true, jackpotNotifications = true, filter = false;
    public boolean isLoaded = false, activeTitleIsLoaded = false;

    private List<Home> homes;
    private List<UMaterial> filteredItems;
    private List<Title> ownedTitles;
    private List<String> ownedMonthlyCrates, claimedMonthlyCrates;
    private List<ItemStack> unclaimedPurchases;
    private List<UUID> customEnchantEntities;

    private HashMap<Integer, Integer> showcaseSizes;
    private HashMap<Integer, ItemStack[]> showcases;
    private HashMap<RarityGem, Boolean> raritygems;
    private HashMap<GlobalChallengePrize, Integer> challengeprizes;
    private LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests;

    private HashMap<CustomKit, Integer> kitLevels;
    private HashMap<CustomKit, Long> kitCooldowns;
    private HashMap<String, Integer> unclaimedLootboxes;
    private HashMap<String, Long> claimedLootboxesExpiration;

    public RPPlayer(UUID uuid) {
        this.uuid = uuid;
        final File f = new File(folder, uuid.toString() + ".yml");
        boolean backup = false;
        if(!players.containsKey(uuid)) {
            if(!f.exists()) {
                try {
                    final File folder = new File(RPPlayer.folder);
                    if(!folder.exists()) {
                        folder.mkdirs();
                    }
                    f.createNewFile();
                    backup = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file = new File(folder, uuid.toString() + ".yml");
            yml = YamlConfiguration.loadConfiguration(file);
            players.put(uuid, this);
        }
        if(backup) backup();
    }
    public static RPPlayer get(UUID player) { return players.getOrDefault(player, new RPPlayer(player)).load(); }
    public void backup(boolean async) {
        if(async) SCHEDULER.runTaskAsynchronously(RANDOM_PACKAGE, () -> backup());
        else backup();
    }
    public void backup() {
        yml.set("name", Bukkit.getOfflinePlayer(uuid).getName());
        final Title T = getActiveTitle();
        final PlayerRank rank = getRank();
        final String strings = (T != null ? T.getIdentifier() : "null") + ";" + (rank != null ? rank.getIdentifier() : "null");
        final String booleans = coinflipNotifications + ";" + filter + ";" + jackpotNotifications;
        final String ints = jackpotTickets.intValue() + ";" + jackpotWins + ";" + addedMaxHomes + ";" + questTokens + ";" + reputationPoints;
        final String longs = jackpotWonCash.doubleValue() + ";" + xpExhaustionExpiration;
        yml.set("strings", strings);
        yml.set("booleans", booleans);
        yml.set("ints", ints);
        yml.set("longs", longs);

        if(coinflipStats != null) {
            final CoinFlipStats coinflipStats = getCoinFlipStats();
            yml.set("coinflip stats", coinflipStats.wins + ";" + coinflipStats.losses + ";" + coinflipStats.wonCash + ";" + coinflipStats.lostCash + ";" + coinflipStats.taxesPaid);
        }

        if(homes != null) {
            final List<String> homez = new ArrayList<>();
            for(Home h : getHomes()) {
                homez.add(h.name + ";" + h.icon.name() + ";" + api.toString(h.location));
            }
            yml.set("homes", homez);
        }

        yml.set("filtered items", getFilteredItemz());

        if(ownedTitles != null) {
            final List<String> titles = new ArrayList<>();
            for(Title t : getTitles()) {
                titles.add(t.getIdentifier());
            }
            yml.set("owned titles", titles);
        }

        if(ownedMonthlyCrates != null) {
            yml.set("owned monthly crates", ownedMonthlyCrates);
        }

        if(claimedMonthlyCrates != null) {
            yml.set("claimed monthly crates", claimedMonthlyCrates);
        }

        if(unclaimedLootboxes != null) {
            yml.set("unclaimed lootboxes", unclaimedLootboxes);
        }

        if(unclaimedPurchases != null) {
            final List<ItemStack> ucp = getUnclaimedPurchases();
            yml.set("unclaimed purchases", null);
            for(ItemStack is : ucp) {
                if(is != null && !is.getType().equals(Material.AIR)) {
                    final String s = "unclaimed purchases." + UUID.randomUUID() + ".";
                    yml.set(s + "item", UMaterial.match(is).name());
                    final ItemMeta m = is.getItemMeta();
                    if(m.hasDisplayName()) yml.set(s + "name", m.getDisplayName().replace("§", "&"));
                    if(m.hasLore()) {
                        final List<String> l = new ArrayList<>();
                        for(String p : m.getLore()) l.add(p.replace("§", "&"));
                        yml.set(s + "lore", l);
                    }
                }
            }
        }

        if(customEnchantEntities != null) {
            final List<UUID> cee = getCustomEnchantEntities();
            yml.set("custom enchant entities", null);
            for(UUID u : cee) {
                final HashMap<UUID, LivingCustomEnchantEntity> e = LivingCustomEnchantEntity.living;
                if(e != null) {
                    final LivingCustomEnchantEntity l = e.get(u);
                    if(l != null) {
                        final String p = "custom enchant entities." + u.toString() + ".";
                        final LivingEntity summoner = l.getSummoner();
                        yml.set(p + "type", l.getType().getPath());
                        yml.set(p + "summoner", summoner != null ? summoner.getUniqueId().toString() : "null");
                    }
                }
            }
        }

        if(raritygems != null) {
            final HashMap<RarityGem, Boolean> r = getRarityGems();
            yml.set("rarity gems", null);
            for(RarityGem g : r.keySet()) {
                yml.set("rarity gems." + g.getIdentifier(), r.get(g));
            }
        }

        if(globalchallengeprizes != null) {
            final HashMap<GlobalChallengePrize, Integer> prizes = getGlobalChallengePrizes();
            yml.set("global challenge prizes", null);
            for(GlobalChallengePrize p : prizes.keySet()) {
                yml.set("global challenge prizes." + p.getPlacement(), prizes.get(p));
            }
        }

        if(kitLevels != null && kitCooldowns != null) {
            final HashMap<CustomKit, Integer> kitLevels = getKitLevels();
            final HashMap<CustomKit, Long> kitCooldowns = getKitCooldowns();
            yml.set("kits", null);
            for(CustomKit k : kitCooldowns.keySet()) {
                final String i = k.getIdentifier();
                yml.set("kits." + i + ".level", kitLevels.getOrDefault(k, -1));
                yml.set("kits." + i + ".cooldown expiration", kitCooldowns.get(k));
            }
        }

        if(quests != null) {
            final HashMap<PlayerQuest, ActivePlayerQuest> quests = getQuests();
            yml.set("quests", null);
            if(quests != null) {
                for(PlayerQuest q : quests.keySet()) {
                    final ActivePlayerQuest A = quests.get(q);
                    yml.set("quests." + q.getIdentifier(), A.getStartedTime() + ";" + A.getProgress() + ";" + A.isCompleted() + ";" + A.hasClaimedRewards());
                }
            }
        }

        if(showcases != null) {
            final HashMap<Integer, ItemStack[]> showcase = getShowcases();
            for(int p : showcase.keySet()) {
                yml.set("showcases." + p, null);
                yml.set("showcases." + p + ".size", getShowcaseSize(p));
                int s = 0;
                for(ItemStack i : showcase.get(p)) {
                    if(i != null && !i.getType().equals(Material.AIR)) {
                        yml.set("showcases." + p + "." + s, i.toString());
                    }
                    s++;
                }
            }
        }

        save();
    }
    public RPPlayer load() {
        if(!isLoaded) {
            isLoaded = true;
            final String bools = yml.getString("booleans"), intz = yml.getString("ints"), longz = yml.getString("longs");
            if(bools != null) {
                final String[] booleans = bools.split(";");
                final int l = booleans.length;
                coinflipNotifications = Boolean.parseBoolean(booleans[0]);
                filter = Boolean.parseBoolean(booleans[1]);
                jackpotNotifications = Boolean.parseBoolean(booleans[2]);
            }
            if(intz != null) {
                final String[] ints = intz.split(";");
                jackpotTickets = BigDecimal.valueOf(Integer.parseInt(ints[0]));
                jackpotWins = Integer.parseInt(ints[1]);
                final int l = ints.length;
                if(l >= 3) {
                    addedMaxHomes = Integer.parseInt(ints[2]);
                    if(l >= 4) {
                        questTokens = Integer.parseInt(ints[3]);
                        if(l >= 5) {
                            reputationPoints = Integer.parseInt(ints[4]);
                        }
                    }
                }
            }
            if(longz != null) {
                final String[] longs = longz.split(";");
                jackpotWonCash = BigDecimal.valueOf(Double.parseDouble(longs[0]));
                xpExhaustionExpiration = Long.parseLong(longs[1]);
            }
            return this;
        }
        return players.get(uuid);
    }
    public void unload(boolean async) {
        if(async) SCHEDULER.runTaskAsynchronously(RANDOM_PACKAGE, () -> unload());
        else unload();
    }
    public void unload() {
        if(isLoaded) {
            try {
                backup();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isLoaded = false;

            if(questTasks.containsKey(uuid)) {
                for(int i : questTasks.get(uuid)) SCHEDULER.cancelTask(i);
                questTasks.remove(uuid);
            }
            players.remove(uuid);
        }
    }
    private void save() {
        try {
            yml.save(file);
            yml = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public UUID getUUID() { return uuid; }
    public OfflinePlayer getOfflinePlayer() {
        return uuid != null ? Bukkit.getOfflinePlayer(uuid) : null;
    }

    public boolean isXPExhausted() {
        return System.currentTimeMillis() < xpExhaustionExpiration;
    }

    public PlayerRank getRank() {
        if(rank == null) {
            final String strings = yml.getString("strings");
            if(strings != null && !strings.isEmpty()) {
                final String[] a = strings.split(";");
                if(a.length >= 2) {
                    final String r = a[1];
                    if(!r.equals("null")) {
                    }
                }
            }
        }
        return rank;
    }
    public CoinFlipStats getCoinFlipStats() {
        if(coinflipStats == null) {
            final String c = yml.getString("coinflip stats");
            if(c != null && !c.isEmpty()) {
                final String[] s = c.split(";");
                coinflipStats = new CoinFlipStats(api.getBigDecimal(s[0]), api.getBigDecimal(s[1]), api.getBigDecimal(s[2]), api.getBigDecimal(s[3]), api.getBigDecimal(s[4]));
            } else {
                final BigDecimal z = BigDecimal.ZERO;
               coinflipStats = new CoinFlipStats(z, z, z ,z, z);
            }
        }
        return coinflipStats;
    }
    public boolean doesReceiveCoinFlipNotifications() { return coinflipNotifications; }
    public void setReceivesCoinFlipNotifications(boolean bool) { coinflipNotifications = bool; }

    public boolean doesReceiveJackpotNotifications() { return jackpotNotifications; }
    public void setReceivesJackpotNotifications(boolean bool) { jackpotNotifications = bool; }

    public List<Home> getHomes() {
        if(homes == null) {
            homes = new ArrayList<>();
            for(String s : yml.getStringList("homes")) {
                final String[] A = s.split(";");
                final String name = A[0];
                final UMaterial mat = UMaterial.match(A[1]);
                homes.add(new Home(name, api.toLocation(s.substring(name.length()+A[1].length()+2)), mat));
            }
        }
        return homes;
    }
    public Home getHome(String name) {
        for(Home h : getHomes()) {
            if(h.name.equals(name)) {
                return h;
            }
        }
        return null;
    }
    public void addHome(Location location, String name, UMaterial icon) {
        final Home h = getHome(name);
        if(h != null) {
            h.location = location;
            h.icon = icon;
        } else {
            homes.add(new Home(name, location, icon));
        }
    }
    public void deleteHome(Home h) {
        getHomes().remove(h);
    }
    public int getMaxHomes() {
        final Player p = Bukkit.getPlayer(uuid);
        for(int i = 100; i >= 1; i--) {
            if(p.hasPermission("RandomPackage.sethome." + i)) {
                return addedMaxHomes+i;
            }
        }
        return Homes.getHomes().defaultMax+addedMaxHomes;
    }

    public boolean hasActiveFilter() { return filter; }
    public void setActiveFilter(boolean bool) { filter = bool; }

    public List<UMaterial> getFilteredItems() {
        if(filteredItems == null) {
            filteredItems = new ArrayList<>();
            for(String s : yml.getStringList("filtered items")) {
                filteredItems.add(UMaterial.valueOf(s));
            }
        }
        return filteredItems;
    }
    public List<String> getFilteredItemz() {
        final List<String> f = new ArrayList<>();
        for(UMaterial u : getFilteredItems()) {
            f.add(u.name());
        }
        return f;
    }

    public List<UUID> getCustomEnchantEntities() {
        if(customEnchantEntities == null) {
            customEnchantEntities = new ArrayList<>();
            final ConfigurationSection c = yml.getConfigurationSection("custom enchant entities");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    customEnchantEntities.add(UUID.fromString(s));
                }
            }
        }
        return customEnchantEntities;
    }
    public void addCustomEnchantEntity(UUID uuid) {
        if(!getCustomEnchantEntities().contains(uuid)) {
            customEnchantEntities.add(uuid);
        }
    }
    public void removeCustomEnchantEntity(UUID uuid) {
        getCustomEnchantEntities().remove(uuid);
    }

    public HashMap<Integer, ItemStack[]> getShowcases() {
        final Showcase showcase = Showcase.getShowcase();
        if(showcase.isEnabled() && showcases == null && showcaseSizes == null) {
            showcases = new HashMap<>();
            showcaseSizes = new HashMap<>();
            final ConfigurationSection c = yml.getConfigurationSection("showcases");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    final int page = Integer.parseInt(s);
                    showcaseSizes.put(page, yml.getInt("showcases." + s + ".size"));
                    final ConfigurationSection i = yml.getConfigurationSection("showcases." + s);
                    final ItemStack[] items = new ItemStack[54];
                    if(i != null) {
                        for(String sl : i.getKeys(false)) {
                            final int slot = Integer.parseInt(sl);
                            ItemStack is;
                            try {
                                is = yml.getItemStack("showcases." + s + "." + sl);
                            } catch (Exception e) {
                                is = api.d(yml, "showcases." + s + "." + sl);
                            }
                            items[slot] = is;
                        }
                    }
                    showcases.put(page, items);
                }
            } else {
                final YamlConfiguration config = showcase.config;
                final int defaultShowcase = config.getInt("settings.default showcases"), defaultSize = config.getInt("settings.default showcase size");
                if(defaultShowcase > 0) {
                    final ItemStack[] a = new ItemStack[54];
                    for(int i = 1; i <= defaultShowcase; i++) {
                        showcases.put(i, a);
                        showcaseSizes.put(i, defaultSize);
                    }
                }
            }
        }
        return showcases;
    }
    public HashMap<Integer, Integer> getShowcaseSizes() {
        getShowcases();
        return showcaseSizes;
    }
    public ItemStack[] getShowcase(int page) {
        return getShowcases().getOrDefault(page, null);
    }
    public int getShowcaseSize(int page) {
        return getShowcaseSizes().getOrDefault(page, 0);
    }
    public HashMap<Integer, ItemStack> getShowcaseItems(int page) {
        final HashMap<Integer, ItemStack> items = new HashMap<>();
        if(getShowcases().containsKey(page)) {
            int p = 0;
            for(ItemStack a : showcases.get(page)) {
                if(a != null && !a.getType().equals(Material.AIR)) {
                    items.put(p, a);
                }
                p++;
            }
        }
        return items;
    }
    public void addToShowcase(int page, ItemStack item) {
        if(getShowcases().containsKey(page)) {
            int p = 0;
            for(ItemStack is : showcases.get(page)) {
                if(is == null || is.getType().equals(Material.AIR)) {
                    showcases.get(page)[p] = item;
                    return;
                }
                p++;
            }
        }
    }
    public void removeFromShowcase(int page, ItemStack item) {
        if(getShowcases().containsKey(page)) {
            int p = 0;
            for(ItemStack is : showcases.get(page)) {
                if(is != null && is.equals(item)) {
                    showcases.get(page)[p] = null;
                    return;
                }
                p++;
            }
        }
    }
    public void resetShowcases() {
        showcases = new HashMap<>();
        showcaseSizes = new HashMap<>();
    }
    public void resetShowcase(int page) {
        showcases.put(page, new ItemStack[54]);
        showcaseSizes.put(page, 9);
    }

    public HashMap<RarityGem, Boolean> getRarityGems() {
        if(raritygems == null) {
            raritygems = new HashMap<>();
            final ConfigurationSection c = yml.getConfigurationSection("rarity gems");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    raritygems.put(getRarityGem(s), yml.getBoolean("rarity gems." + s));
                }
            }
        }
        return raritygems;
    }
    public boolean hasActiveRarityGem(RarityGem gem) {
        return gem != null && getRarityGems().getOrDefault(gem, false);
    }
    public void toggleRarityGem(RarityGem gem, List<String> msg) {
        if(gem != null) {
            raritygems.put(gem, !getRarityGems().getOrDefault(gem, false));
            sendStringListMessage(Bukkit.getPlayer(uuid), msg, null);
        }
    }

    public Title getActiveTitle() {
        if(!activeTitleIsLoaded && activeTitle == null) {
            activeTitleIsLoaded = true;
            if(titles != null) {
                final String strings = yml.getString("strings");
                if(strings != null && !strings.isEmpty()) {
                    final String s = strings.split(";")[0];
                    if(!s.equals("null")) {
                        activeTitle = getTitle(s);
                    }
                }
            }
        }
        return activeTitle;
    }
    public void setActiveTitle(@Nullable Title title) {
        activeTitle = title;
    }

    public List<Title> getTitles() {
        if(ownedTitles == null) {
            ownedTitles = new ArrayList<>();
            for(String s : yml.getStringList("owned titles")) {
                final Title t = getTitle(s);
                if(t != null) {
                    ownedTitles.add(t);
                }
            }
        }
        return ownedTitles;
    }
    public void addTitle(@NotNull Title title) {
        if(!getTitles().contains(title)) {
            ownedTitles.add(title);
        }
    }

    public List<ItemStack> getUnclaimedPurchases() {
        if(unclaimedPurchases == null) {
            unclaimedPurchases = new ArrayList<>();
            final ConfigurationSection cs = yml.getConfigurationSection("unclaimed purchases");
            if(cs != null) {
                for(String s : cs.getKeys(false)) {
                    unclaimedPurchases.add(api.d(yml, "unclaimed purchases." + s));
                }
            }
        }
        return unclaimedPurchases;
    }
    public void removeUnclaimedPurchase(ItemStack is) {
        getUnclaimedPurchases().remove(is);
    }

    public HashMap<GlobalChallengePrize, Integer> getGlobalChallengePrizes() {
        if(challengeprizes == null) {
            challengeprizes = new HashMap<>();
            final ConfigurationSection a = yml.getConfigurationSection("global challenge prizes");
            if(a != null) {
                for(String s : a.getKeys(false)) {
                    challengeprizes.put(valueOfGlobalChallengePrize(Integer.parseInt(s)), yml.getInt("global challenge prizes." + s));
                }
            }
        }
        return challengeprizes;
    }
    public void addGlobalChallengePrize(GlobalChallengePrize prize) {
        getGlobalChallengePrizes().put(prize, challengeprizes.getOrDefault(prize, 0)+1);
    }

    public HashMap<CustomKit, Integer> getKitLevels() {
        if(kitLevels == null) {
            kitLevels = new HashMap<>();
            kitCooldowns = new HashMap<>();
            final ConfigurationSection c = yml.getConfigurationSection("kits");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    final CustomKit k = getCustomKit(s);
                    if(k != null) {
                        kitLevels.put(k, yml.getInt("kits." + s + ".level"));
                        kitCooldowns.put(k, yml.getLong("kits." + s + ".cooldown expiration"));
                    }
                }
            }
        }
        return kitLevels;
    }
    public HashMap<CustomKit, Long> getKitCooldowns() {
        getKitLevels();
        return kitCooldowns;
    }
    public int getKitLevel(CustomKit kit) {
        return kit != null ? getKitLevels().getOrDefault(kit, -1) : -1;
    }
    public void setKitCooldown(CustomKit kit, long cooldownExpiration) {
        if(kit != null) getKitCooldowns().put(kit, cooldownExpiration);
    }

    public List<String> getMonthlyCrates() {
        if(ownedMonthlyCrates == null) {
            ownedMonthlyCrates = yml.getStringList("owned monthly crates");
        }
        return ownedMonthlyCrates;
    }
    public List<String> getClaimedMonthlyCrates() {
        if(claimedMonthlyCrates == null) {
            claimedMonthlyCrates = yml.getStringList("claimed monthly crates");
        }
        return claimedMonthlyCrates;
    }

    public HashMap<String, Integer> getUnclaimedLootboxes() {
        if(unclaimedLootboxes == null) {
            unclaimedLootboxes = new HashMap<>();
            final ConfigurationSection a = yml.getConfigurationSection("unclaimed lootboxes");
            if(a != null) {
                for(String s : a.getKeys(false)) {
                    unclaimedLootboxes.put(s, yml.getInt("unclaimed lootboxes." + s));
                }
            }
        }
        return unclaimedLootboxes;
    }

    private void loadQuests() {
        if(quests == null) {
            quests = new LinkedHashMap<>();
            final PlayerQuests QQ = PlayerQuests.getPlayerQuests();
            if(QQ.isEnabled()) {
                questTasks.put(uuid, new ArrayList<>());
                final ConfigurationSection c = yml.getConfigurationSection("quests");
                final boolean isEnabled = getPlugin.isEnabled();
                if(c != null) {
                    final long time = System.currentTimeMillis();
                    for(String s : c.getKeys(false)) {
                        final PlayerQuest q = getPlayerQuest(s);
                        if(q != null) {
                            final String[] b = yml.getString("quests." + s).split(";");
                            final ActivePlayerQuest a = new ActivePlayerQuest(Long.parseLong(b[0]), q, Double.parseDouble(b[1]), Boolean.parseBoolean(b[2]), Boolean.parseBoolean(b[3]));
                            if(!a.isExpired()) {
                                quests.put(q, a);
                                if(isEnabled) {
                                    startExpire(time, SCHEDULER, PLUGIN_MANAGER, q, a);
                                }
                            }
                        }
                    }
                }
                int permfor = 0;
                final int max = QQ.questSlots.size();
                final OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                Player player = null;
                boolean useVault = false;
                if(p.isOnline()) {
                    player = p.getPlayer();
                } else if(permissions != null) {
                    useVault = true;
                } else {
                    return;
                }
                for(int i = 1; i <= max; i++) {
                    if(useVault && permissions.playerHas(null, p, "RandomPackage.playerquests." + i) || !useVault && player.hasPermission("RandomPackage.playerquests." + i)) {
                        permfor = i;
                    }
                }
                if(quests.size() != max) {
                    final long time = System.currentTimeMillis();
                    final Random random = new Random();
                    final HashMap<String, PlayerQuest> pq = new HashMap<>(playerquests);
                    for(ActivePlayerQuest R : quests.values()) pq.remove(R.getQuest().getName());
                    for(int i = 1; i <= permfor && quests.size() <= permfor-1; i++) {
                        loadNewQuest(time, random);
                    }
                }
            }
        }
    }
    private void loadNewQuest(long time, Random random) {
        final HashMap<String, PlayerQuest> pq = new HashMap<>(playerquests);
        for(ActivePlayerQuest R : quests.values()) pq.remove(R.getQuest().getIdentifier());
        final int pqs = pq.size();
        final BukkitScheduler s = api.SCHEDULER;
        final PluginManager pm = api.PLUGIN_MANAGER;
        final boolean isEnabled = getPlugin.isEnabled();
        for(int z = 1; z <= 100; z++) {
            final PlayerQuest ran = (PlayerQuest) pq.values().toArray()[random.nextInt(pqs)];
            if(ran != null) {
                final ActivePlayerQuest a = new ActivePlayerQuest(time, ran, 0, false, false);
                final PlayerQuestStartEvent e = new PlayerQuestStartEvent(uuid, a);
                pm.callEvent(e);
                if(!e.isCancelled()) {
                    quests.put(ran, a);
                    if(isEnabled) startExpire(time, s, pm, ran, a);
                    break;
                }
            }
        }
    }
    private void startExpire(long time, BukkitScheduler s, PluginManager pm, PlayerQuest q, ActivePlayerQuest a) {
        final long ticks = ((a.getExpirationTime()-time)/1000)*20;
        questTasks.get(uuid).add(s.scheduleSyncDelayedTask(getPlugin, () -> {
            final PlayerQuestExpireEvent ee = new PlayerQuestExpireEvent(uuid, a);
            pm.callEvent(ee);
            quests.remove(q);
            loadNewQuest(System.currentTimeMillis(), new Random());
        }, ticks));
    }
    public HashMap<PlayerQuest, ActivePlayerQuest> getQuests() {
        loadQuests();
        return quests;
    }
    public void setQuests(LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests) {
        this.quests = quests;
        if(quests == null) {
            yml.set("quests", null);
            save();
        }
    }

    public int getReputationPoints() { return reputationPoints; }
    public void setReputationPoints(int reputationPoints) { this.reputationPoints = reputationPoints; }

    public static void loadAllPlayerData() {
        try {
            for(File f : new File(folder).listFiles()) {
                RPPlayer.get(UUID.fromString(f.getName().split("\\.yml")[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void resetAllPlayerDataToDefault(boolean filter, boolean homes, boolean lootboxes, boolean monthlycrates, boolean playerquests, boolean showcase, boolean titles, boolean unclaimedPurchases) {
        try {
            for(File f : new File(folder).listFiles()) {
                final UUID uuid = UUID.fromString(f.getName().split("\\.yml")[0]);
                final boolean online = players.containsKey(uuid);
                final RPPlayer pdata = RPPlayer.get(uuid);

                pdata.xpExhaustionExpiration = 0l;

                if(filter) {
                    pdata.filter = false;
                    pdata.filteredItems = new ArrayList<>();
                }
                if(homes) {
                    pdata.homes = new ArrayList<>();
                }
                if(lootboxes) {
                    pdata.claimedLootboxesExpiration = new HashMap<>();
                    pdata.unclaimedLootboxes = new HashMap<>();
                }
                if(monthlycrates) {
                    pdata.claimedMonthlyCrates = new ArrayList<>();
                    pdata.ownedMonthlyCrates = new ArrayList<>();
                }
                if(playerquests) {
                    pdata.questTokens = 0;
                    pdata.quests = new LinkedHashMap<>();
                }
                if(showcase) {
                    pdata.showcases = new HashMap<>();
                    pdata.showcaseSizes = new HashMap<>();
                }
                if(titles) {
                    pdata.activeTitle = null;
                    pdata.ownedTitles = new ArrayList<>();
                }
                if(unclaimedPurchases) {
                    pdata.unclaimedPurchases = new ArrayList<>();
                }
                if(!online) {
                    pdata.unload();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void unloadAllPlayerData() {
        try {
            for(RPPlayer pdata : new ArrayList<>(players.values())) {
                pdata.unload();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
