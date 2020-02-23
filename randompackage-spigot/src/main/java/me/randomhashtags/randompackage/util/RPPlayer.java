package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.addon.PlayerRank;
import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.addon.Title;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.api.PlayerQuests;
import me.randomhashtags.randompackage.api.Titles;
import me.randomhashtags.randompackage.event.PlayerQuestExpireEvent;
import me.randomhashtags.randompackage.event.PlayerQuestStartEvent;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackageAPI.API;
import static me.randomhashtags.randompackage.supported.economy.Vault.permissions;

public class RPPlayer implements RPStorage {
    private static final String folder = DATA_FOLDER + SEPARATOR + "_Data" + SEPARATOR + "players";
    public static final HashMap<UUID, RPPlayer> players = new HashMap<>();
    private static final HashMap<UUID, List<Integer>> questTasks = new HashMap<>();

    public UUID uuid;
    public File file = null;
    public YamlConfiguration yml = null;

    private PlayerRank rank;
    public long xpExhaustionExpiration = 0;
    public int questTokens;
    public boolean isLoaded = false, activeTitleIsLoaded = false;

    private List<ItemStack> unclaimedPurchases;
    private List<UUID> customEnchantEntities;

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
    public static RPPlayer get(UUID player) {
        return players.getOrDefault(player, new RPPlayer(player)).load();
    }
    public void backup(boolean async) {
        if(async) SCHEDULER.runTaskAsynchronously(RANDOM_PACKAGE, () -> backup());
        else backup();
    }
    public void backup() {
        yml.set("name", Bukkit.getOfflinePlayer(uuid).getName());
        final Title T = getActiveTitle();
        final PlayerRank rank = getRank();
        final String strings = (T != null ? T.getIdentifier() : "null") + ";" + (rank != null ? rank.getIdentifier() : "null");
        final String ints = jackpotTickets.intValue() + ";" + jackpotWins + ";" + addedMaxHomes + ";" + questTokens + ";" + reputationPoints;
        final String longs = jackpotWonCash.doubleValue() + ";" + xpExhaustionExpiration;
        yml.set("strings", strings);
        yml.set("ints", ints);
        yml.set("longs", longs);

        yml.set("filtered items", getFilteredItemz());

        if(unclaimedLootboxes != null) {
            yml.set("unclaimed lootboxes", unclaimedLootboxes);
        }

        if(unclaimedPurchases != null) {
            yml.set("unclaimed purchases", null);
            for(ItemStack is : unclaimedPurchases) {
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
            yml.set("custom enchant entities", null);
            for(UUID u : customEnchantEntities) {
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

        save();
    }
    public RPPlayer load() {
        if(!isLoaded) {
            isLoaded = true;
            final String longz = yml.getString("longs");
            if(longz != null) {
                final String[] longs = longz.split(";");
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
                for(int i : questTasks.get(uuid)) {
                    SCHEDULER.cancelTask(i);
                }
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
                final String[] values = strings.split(";");
                if(values.length >= 2) {
                    final String r = values[1];
                    if(!r.equals("null")) {
                    }
                }
            }
        }
        return rank;
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

    public void toggleRarityGem(RarityGem gem, List<String> msg) {
        if(gem != null) {
            raritygems.put(gem, !getRarityGems().getOrDefault(gem, false));
            sendStringListMessage(Bukkit.getPlayer(uuid), msg, null);
        }
    }

    public Title getActiveTitle() {
        if(!activeTitleIsLoaded && activeTitle == null) {
            activeTitleIsLoaded = true;
            if(Titles.getTitles().isEnabled()) {
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

    public List<ItemStack> getUnclaimedPurchases() {
        if(unclaimedPurchases == null) {
            unclaimedPurchases = new ArrayList<>();
            final ConfigurationSection cs = yml.getConfigurationSection("unclaimed purchases");
            if(cs != null) {
                for(String s : cs.getKeys(false)) {
                    unclaimedPurchases.add(API.createItemStack(yml, "unclaimed purchases." + s));
                }
            }
        }
        return unclaimedPurchases;
    }
    public void removeUnclaimedPurchase(ItemStack is) {
        getUnclaimedPurchases().remove(is);
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
                final boolean isEnabled = RANDOM_PACKAGE.isEnabled();
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
                                    startExpire(time, q, a);
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
                    final HashMap<String, PlayerQuest> pq = new HashMap<>(getAllPlayerQuests());
                    for(ActivePlayerQuest R : quests.values()) {
                        pq.remove(R.getQuest().getName());
                    }
                    for(int i = 1; i <= permfor && quests.size() <= permfor-1; i++) {
                        loadNewQuest(time);
                    }
                }
            }
        }
    }
    private void loadNewQuest(long time) {
        final HashMap<String, PlayerQuest> playerquests = new HashMap<>(getAllPlayerQuests());
        for(ActivePlayerQuest R : quests.values()) {
            playerquests.remove(R.getQuest().getIdentifier());
        }
        final int size = playerquests.size();
        if(size > 0) {
            final boolean isEnabled = RANDOM_PACKAGE.isEnabled();
            for(int z = 1; z <= 100; z++) {
                final PlayerQuest ran = (PlayerQuest) playerquests.values().toArray()[RANDOM.nextInt(size)];
                if(ran != null) {
                    final ActivePlayerQuest a = new ActivePlayerQuest(time, ran, 0, false, false);
                    final PlayerQuestStartEvent e = new PlayerQuestStartEvent(uuid, a);
                    PLUGIN_MANAGER.callEvent(e);
                    if(!e.isCancelled()) {
                        quests.put(ran, a);
                        if(isEnabled) {
                            startExpire(time, ran, a);
                        }
                        break;
                    }
                }
            }
        }
    }
    private void startExpire(long time, PlayerQuest q, ActivePlayerQuest a) {
        final long ticks = ((a.getExpirationTime()-time)/1000)*20;
        questTasks.get(uuid).add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
            final PlayerQuestExpireEvent ee = new PlayerQuestExpireEvent(uuid, a);
            PLUGIN_MANAGER.callEvent(ee);
            quests.remove(q);
            loadNewQuest(System.currentTimeMillis());
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

    public static void loadAllPlayerData() {
        try {
            for(File f : new File(folder).listFiles()) {
                RPPlayer.get(UUID.fromString(f.getName().split("\\.yml")[0]));
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
