package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.api.Homes;
import me.randomhashtags.randompackage.api.events.PlayerQuestExpireEvent;
import me.randomhashtags.randompackage.api.events.PlayerQuestStartEvent;
import me.randomhashtags.randompackage.api.PlayerQuests;
import me.randomhashtags.randompackage.utils.abstraction.AbstractFactionUpgrade;
import me.randomhashtags.randompackage.utils.classes.Title;
import me.randomhashtags.randompackage.utils.classes.CoinFlipStats;
import me.randomhashtags.randompackage.utils.classes.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.utils.classes.customenchants.RarityGem;
import me.randomhashtags.randompackage.utils.classes.globalchallenges.GlobalChallengePrize;
import me.randomhashtags.randompackage.utils.classes.Home;
import me.randomhashtags.randompackage.utils.classes.kits.EvolutionKit;
import me.randomhashtags.randompackage.utils.classes.kits.GlobalKit;
import me.randomhashtags.randompackage.utils.enums.KitType;
import me.randomhashtags.randompackage.utils.classes.kits.MasteryKit;
import me.randomhashtags.randompackage.utils.classes.playerquests.ActivePlayerQuest;
import me.randomhashtags.randompackage.utils.classes.playerquests.PlayerQuest;
import me.randomhashtags.randompackage.utils.supported.FactionsAPI;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

public class RPPlayer {
    private static final String s = File.separator, folder = getPlugin.getDataFolder() + s + "_Data" + s + "players";
    public static final HashMap<UUID, RPPlayer> players = new HashMap<>();
    private static final HashMap<String, HashMap<AbstractFactionUpgrade, Integer>> factionUpgrades = new HashMap<>();
    private static final HashMap<UUID, List<Integer>> questTasks = new HashMap<>();

    public static YamlConfiguration fadditions;

    public UUID uuid;
    public File file = null;
    public YamlConfiguration yml = null;

    private CoinFlipStats coinflipStats;
    private Title activeTitle;
    public long jackpotWonCash = 0, xpExhaustionExpiration = 0;
    public int jackpotTickets = 0, jackpotWins = 0, addedMaxHomes = 0, questTokens = 0;
    public boolean coinflipNotifications = true, jackpotCountdown = true, filter = false;
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
    private HashMap<PlayerQuest, ActivePlayerQuest> quests;

    private HashMap<String, Integer> gkits, vkits, mkits, unclaimedLootboxes;
    private HashMap<String, Long> gkitCooldowns, vkitCooldowns, mkitCooldowns, claimedLootboxesExpiration;

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
    public static RPPlayer get(UUID player) { return players.getOrDefault(player, new RPPlayer(player)); }
    public void backup() {
        yml.set("name", Bukkit.getOfflinePlayer(uuid).getName());
        final Title T = getActiveTitle();
        final String strings = T != null ? T.getTitle() : "null";
        final String booleans = coinflipNotifications + ";" + filter + ";" + jackpotCountdown;
        final String ints = jackpotTickets + ";" + jackpotWins + ";" + addedMaxHomes + ";" + questTokens;
        final String longs = jackpotWonCash + ";" + xpExhaustionExpiration;
        yml.set("strings", strings);
        yml.set("booleans", booleans);
        yml.set("ints", ints);
        yml.set("longs", longs);
        final CoinFlipStats coinflipStats = getCoinFlipStats();
        yml.set("coinflip stats", coinflipStats.wins + ";" + coinflipStats.losses + ";" + coinflipStats.wonCash + ";" + coinflipStats.lostCash + ";" + coinflipStats.taxesPaid);
        final List<String> homez = new ArrayList<>(), titles = new ArrayList<>();
        for(Home h : getHomes()) homez.add(h.name + ";" + h.icon.name() + ";" + api.toString(h.location));
        yml.set("homes", homez);
        yml.set("filtered items", getFilteredItemz());
        for(Title t : getTitles()) titles.add(t.getTitle());
        yml.set("owned titles", titles);
        yml.set("owned monthly crates", getMonthlyCrates());
        yml.set("claimed monthly crates", getClaimedMonthlyCrates());
        yml.set("unclaimed lootboxes", getUnclaimedLootboxes());
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

        final HashMap<RarityGem, Boolean> r = getRarityGems();
        yml.set("rarity gems", null);
        for(RarityGem g : r.keySet()) {
            yml.set("rarity gems." + g.getPath(), r.get(g));
        }

        loadGlobalChallengePrizes();
        yml.set("global challenge prizes", null);
        for(GlobalChallengePrize p : challengeprizes.keySet()) {
            yml.set("global challenge prizes." + p.getPlacement(), challengeprizes.get(p));
        }

        loadKits(KitType.GLOBAL);
        loadKitCooldowns(KitType.GLOBAL);
        yml.set("gkits", null);
        for(String s : gkits.keySet()) {
            yml.set("gkits." + s + ".level", gkits.get(s));
            yml.set("gkits." + s + ".cooldown expiration", gkitCooldowns.getOrDefault(s, 0l));
        }

        loadKits(KitType.EVOLUTION);
        loadKitCooldowns(KitType.EVOLUTION);
        yml.set("vkits", null);
        for(String s : vkits.keySet()) {
            yml.set("vkits." + s + ".level", vkits.get(s));
            yml.set("vkits." + s + ".cooldown expiration", vkitCooldowns.getOrDefault(s, 0l));
        }

        loadKits(KitType.MASTERY);
        loadKitCooldowns(KitType.MASTERY);
        yml.set("mkits", null);
        for(String s : mkits.keySet()) {
            yml.set("mkits." + s + ".level", mkits.get(s));
            yml.set("mkits." + s + ".cooldown expiration", mkitCooldowns.getOrDefault(s, 0l));
        }

        final HashMap<PlayerQuest, ActivePlayerQuest> apq = getQuests();
        yml.set("quests", null);
        if(apq != null) {
            for(PlayerQuest q : apq.keySet()) {
                final ActivePlayerQuest A = apq.get(q);
                yml.set("quests." + q.getFile().getName().split("\\.yml")[0], A.getStartedTime() + ";" + A.getProgress() + ";" + A.isCompleted() + ";" + A.hasClaimedRewards());
            }
        }

        final HashMap<Integer, ItemStack[]> showcase = getShowcases();
        for(int p : showcase.keySet()) {
            yml.set("showcase." + p, null);
            yml.set("showcase." + p + ".size", getShowcaseSize(p));
            int s = 0;
            for(ItemStack i : showcase.get(p)) {
                if(i != null && !i.getType().equals(Material.AIR)) {
                    final UMaterial j = UMaterial.match(i);
                    yml.set("showcase." + p + "." + s + ".item", j.name());
                    if(i.hasItemMeta()) {
                        final ItemMeta m = i.getItemMeta();
                        if(m.hasDisplayName()) yml.set("showcase." + p + "." + s + ".name", m.getDisplayName());
                        final List<String> l = new ArrayList<>();
                        if(m.hasEnchants()) {
                            String en = "VEnchants{";
                            final Map<Enchantment, Integer> enchants = m.getEnchants();
                            for(Enchantment e : enchants.keySet()) {
                                en = en.concat(e.getName() + enchants.get(e) + ";");
                            }
                            l.add(en + "}");
                        }
                        if(m.hasLore()) l.addAll(m.getLore());
                        if(!l.isEmpty()) yml.set("showcase." + p + "." + s + ".lore", l);
                    }
                }
                s++;
            }
        }
        save();
    }
    public RPPlayer load() {
        if(!isLoaded) {
            isLoaded = true;

            final String[] booleans = yml.getString("booleans").split(";"), ints = yml.getString("ints").split(";"), longs = yml.getString("longs").split(";");

            coinflipNotifications = Boolean.parseBoolean(booleans[0]);
            filter = Boolean.parseBoolean(booleans[1]);
            jackpotCountdown = Boolean.parseBoolean(booleans[2]);

            jackpotTickets = Integer.parseInt(ints[0]);
            jackpotWins = Integer.parseInt(ints[1]);
            if(ints.length >= 3) addedMaxHomes = Integer.parseInt(ints[2]);
            if(ints.length >= 4) questTokens = Integer.parseInt(ints[3]);

            jackpotWonCash = Long.parseLong(longs[0]);
            xpExhaustionExpiration = Long.parseLong(longs[1]);
            loadFactionUpgrades();
            return this;
        }
        return players.get(uuid);
    }
    public void unload() {
        if(isLoaded) {
            try {
                backup();
            } catch(Exception e) {
                e.printStackTrace();
            }
            isLoaded = false;
            file = null;
            yml = null;
            coinflipStats = null;
            activeTitle = null;

            homes = null;
            filteredItems = null;
            ownedTitles = null;
            ownedMonthlyCrates = null;
            claimedMonthlyCrates = null;
            unclaimedPurchases = null;
            customEnchantEntities = null;
            showcaseSizes = null;
            showcases = null;
            raritygems = null;
            challengeprizes = null;

            gkits = null;
            vkits = null;
            mkits = null;
            gkitCooldowns = null;
            vkitCooldowns = null;
            mkitCooldowns = null;
            quests = null;

            if(questTasks.containsKey(uuid)) {
                final BukkitScheduler s = api.scheduler;
                for(int i : questTasks.get(uuid)) s.cancelTask(i);
                questTasks.remove(uuid);
            }

            jackpotWonCash = 0;
            jackpotTickets = 0;
            addedMaxHomes = 0;
            jackpotWins = 0;
            questTokens = 0;
            players.remove(uuid);
            uuid = null;
        }
    }
    private void save() {
        try {
            yml.save(file);
            yml = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public OfflinePlayer getOfflinePlayer() {
        return uuid != null ? Bukkit.getOfflinePlayer(uuid) : null;
    }

    public boolean isXPExhausted() {
        return System.currentTimeMillis() < xpExhaustionExpiration;
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

    private void loadHomes() {
        if(homes == null) {
            homes = new ArrayList<>();
            for(String s : yml.getStringList("homes")) {
                final String[] A = s.split(";");
                final String name = A[0];
                final UMaterial mat = UMaterial.match(A[1]);
                homes.add(new Home(name, api.toLocation(s.substring(name.length()+A[1].length()+2)), mat));
            }
        }
    }
    public List<Home> getHomes() {
        loadHomes();
        return homes;
    }
    public Home getHome(String name) {
        loadHomes();
        for(Home h : homes) {
            if(h.name.equals(name)) {
                return h;
            }
        }
        return null;
    }
    public void addHome(Location location, String name, UMaterial icon) {
        loadHomes();
        final Home h = getHome(name);
        if(h != null) {
            h.location = location;
            h.icon = icon;
        } else {
            homes.add(new Home(name, location, icon));
        }
    }
    public void deleteHome(Home h) {
        loadHomes();
        homes.remove(h);
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

    private void loadFilteredItems() {
        if(filteredItems == null) {
            filteredItems = new ArrayList<>();
            for(String s : yml.getStringList("filtered items")) {
                filteredItems.add(UMaterial.valueOf(s));
            }
        }
    }
    public List<UMaterial> getFilteredItems() {
        loadFilteredItems();
        return filteredItems;
    }
    public List<String> getFilteredItemz() {
        loadFilteredItems();
        final List<String> f = new ArrayList<>();
        for(UMaterial u : filteredItems) f.add(u.name());
        return f;
    }

    private void loadCustomEnchantEntities() {
        if(customEnchantEntities == null) {
            customEnchantEntities = new ArrayList<>();
            final ConfigurationSection c = yml.getConfigurationSection("custom enchant entities");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    customEnchantEntities.add(UUID.fromString(s));
                }
            }
        }
    }
    public List<UUID> getCustomEnchantEntities() {
        loadCustomEnchantEntities();
        return customEnchantEntities;
    }
    public void addCustomEnchantEntity(UUID uuid) {
        loadCustomEnchantEntities();
        if(!customEnchantEntities.contains(uuid)) customEnchantEntities.add(uuid);
    }
    public void removeCustomEnchantEntity(UUID uuid) {
        loadCustomEnchantEntities();
        customEnchantEntities.remove(uuid);
    }

    private void loadShowcases() {
        if(showcases == null && showcaseSizes == null) {
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
                           items[slot] = api.d(yml, "showcases." + s + "." + sl);
                       }
                   }
                   showcases.put(page, items);
               }
            }
        }
    }
    public HashMap<Integer, ItemStack[]> getShowcases() {
        loadShowcases();
        return showcases;
    }
    public HashMap<Integer, Integer> getShowcaseSizes() {
        loadShowcases();
        return showcaseSizes;
    }
    public ItemStack[] getShowcase(int page) {
        loadShowcases();
        return showcases.getOrDefault(page, null);
    }
    public int getShowcaseSize(int page) {
        loadShowcases();
        return showcaseSizes.getOrDefault(page, 0);
    }
    public HashMap<Integer, ItemStack> getShowcaseItems(int page) {
        loadShowcases();
        final HashMap<Integer, ItemStack> items = new HashMap<>();
        if(showcases.containsKey(page)) {
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
        loadShowcases();
        if(showcases.containsKey(page)) {
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
    public void removeFromShowcase(ItemStack item) {
        loadShowcases();
        for(int i : showcases.keySet()) {
            final ItemStack[] a = showcases.get(i);
            int p = 0;
            for(ItemStack l : a) {
                if(l.equals(item)) {
                    a[p] = null;
                    return;
                }
                p++;
            }
        }
    }
    public void removeFromShowcase(int page, ItemStack item) {
        loadShowcases();
        if(showcases.containsKey(page)) {
            int p = 0;
            for(ItemStack is : showcases.get(page)) {
                if(is.equals(item)) {
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
        showcaseSizes.put(1, 9);
    }
    public void resetShowcase(int page) {
        showcases.put(page, new ItemStack[54]);
        showcaseSizes.put(page, 9);
    }

    private void loadRarityGems() {
        if(raritygems == null) {
            raritygems = new HashMap<>();
            final ConfigurationSection c = yml.getConfigurationSection("rarity gems");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    raritygems.put(RarityGem.gems.get(s), yml.getBoolean("rarity gems." + s));
                }
            }
        }
    }
    public HashMap<RarityGem, Boolean> getRarityGems() {
        loadRarityGems();
        return raritygems;
    }
    public boolean hasActiveRarityGem(RarityGem gem) {
        if(gem != null) {
            loadRarityGems();
            return raritygems.getOrDefault(gem, false);
        }
        return false;
    }
    public void toggleRarityGem(Event event, RarityGem gem) {
        if(event != null && gem != null) {
            loadRarityGems();
            final boolean prev = raritygems.getOrDefault(gem, false), on = !prev;
            raritygems.put(gem, on);
            final List<String> msg = on ? gem.getToggleOnMsg() : event instanceof PlayerInteractEvent ? gem.getToggleOffInteractMsg() : event instanceof InventoryClickEvent ? gem.getToggleOffMovedMsg() : event instanceof PlayerDropItemEvent ? gem.getToggleOffDroppedMsg() : gem.getToggleOffRanOutMsg();
            api.sendStringListMessage(Bukkit.getPlayer(uuid), msg, null);
        }
    }

    private void loadActiveTitle() {
        if(!activeTitleIsLoaded && activeTitle == null && Title.titles != null) {
            activeTitleIsLoaded = true;
            final String s = yml.getString("strings");
            if(s != null && !s.isEmpty()) {
                final String a = s.split(";")[0];
                if(a != null && !a.equals("null")) {
                    activeTitle = Title.titles.get(a);
                }
            }
        }
    }
    public Title getActiveTitle() {
        loadActiveTitle();
        return activeTitle;
    }
    public void setActiveTitle(Title title) {
        activeTitle = title;
    }

    private void loadTitles() {
        if(ownedTitles == null) {
            ownedTitles = new ArrayList<>();
            final List<String> O = yml.getStringList("owned titles");
            for(String s : O) {
                ownedTitles.add(Title.titles.get(s));
            }
        }
    }
    public List<Title> getTitles() {
        loadTitles();
        return ownedTitles;
    }
    public void addTitle(Title title) {
        loadTitles();
        if(!ownedTitles.contains(title)) {
            ownedTitles.add(title);
        }
    }

    private void loadUnclaimedPurchases() {
        if(unclaimedPurchases == null) {
            unclaimedPurchases = new ArrayList<>();
            final ConfigurationSection cs = yml.getConfigurationSection("unclaimed purchases");
            if(cs != null) {
                for(String s : cs.getKeys(false)) {
                    unclaimedPurchases.add(api.d(yml, "unclaimed purchases." + s));
                }
            }
        }
    }
    public List<ItemStack> getUnclaimedPurchases() {
        loadUnclaimedPurchases();
        return unclaimedPurchases;
    }
    public void removeUnclaimedPurchase(ItemStack is) {
        loadUnclaimedPurchases();
        unclaimedPurchases.remove(is);
    }

    private void loadGlobalChallengePrizes() {
        if(challengeprizes == null) {
            challengeprizes = new HashMap<>();
            final ConfigurationSection a = yml.getConfigurationSection("global challenge prizes");
            if(a != null) {
                for(String s : a.getKeys(false)) {
                    challengeprizes.put(GlobalChallengePrize.valueOf(Integer.parseInt(s)), yml.getInt("global challenge prizes." + s));
                }
            }
        }
    }
    public HashMap<GlobalChallengePrize, Integer> getGlobalChallengePrizes() {
        loadGlobalChallengePrizes();
        return challengeprizes;
    }
    public void addGlobalChallengePrize(GlobalChallengePrize prize) {
        loadGlobalChallengePrizes();
        challengeprizes.put(prize, challengeprizes.getOrDefault(prize, 0)+1);
    }

    private void loadKits(KitType type) {
        final String p = type == KitType.GLOBAL ? "gkits" : type == KitType.EVOLUTION ? "vkits" : "mkits";
        final HashMap<String, Integer> levels;
        if(type == KitType.GLOBAL && gkits == null) {
            gkits = new HashMap<>();
            levels = gkits;
        } else if(type == KitType.EVOLUTION && vkits == null) {
            vkits = new HashMap<>();
            levels = vkits;
        } else if(type == KitType.MASTERY && mkits == null) {
            mkits = new HashMap<>();
            levels = mkits;
        } else return;
        final ConfigurationSection c = yml.getConfigurationSection(p);
        if(c != null) {
            for(String s : c.getKeys(false)) {
                levels.put(s, yml.getInt(p + "." + s + ".level"));
            }
        }
    }
    private void loadKitCooldowns(KitType type) {
        final String p = type == KitType.GLOBAL ? "gkits" : type == KitType.EVOLUTION ? "vkits" : "mkits";
        final HashMap<String, Long> cooldowns;
        if(type == KitType.GLOBAL && gkitCooldowns == null) {
            gkitCooldowns = new HashMap<>();
            cooldowns = gkitCooldowns;
        } else if(type == KitType.EVOLUTION && vkitCooldowns == null) {
            vkitCooldowns = new HashMap<>();
            cooldowns = vkitCooldowns;
        } else if(type == KitType.MASTERY && mkitCooldowns == null) {
            mkitCooldowns = new HashMap<>();
            cooldowns = mkitCooldowns;
        } else return;
        final ConfigurationSection c = yml.getConfigurationSection(p);
        if(c != null) {
            for(String s : c.getKeys(false)) {
                cooldowns.put(s, yml.getLong(p + "." + s + ".cooldown expiration"));
            }
        }
    }
    public HashMap<String, Integer> getKitLevels(KitType type) {
        loadKits(type);
        return type == KitType.GLOBAL ? gkits : type == KitType.EVOLUTION ? vkits : mkits;
    }
    public int getKitLevel(GlobalKit kit) {
        loadKits(KitType.GLOBAL);
        return gkits.getOrDefault(kit.getYamlName(), 0);
    }
    public int getKitLevel(EvolutionKit kit) {
        loadKits(KitType.EVOLUTION);
        return vkits.getOrDefault(kit.getYamlName(), 0);
    }
    public int getKitLevel(MasteryKit kit) {
        loadKits(KitType.MASTERY);
        return mkits.getOrDefault(kit.getYamlName(), 0);
    }
    public HashMap<String, Long> getKitCooldowns(KitType type) {
        loadKitCooldowns(type);
        return type == KitType.GLOBAL ? gkitCooldowns : type == KitType.EVOLUTION ? vkitCooldowns : mkitCooldowns;
    }
    public long getKitCooldown(GlobalKit kit) {
        loadKitCooldowns(KitType.GLOBAL);
        return gkitCooldowns.getOrDefault(kit.getYamlName(), 0l);
    }
    public void setKitCooldown(GlobalKit kit, long cooldown) {
        loadKitCooldowns(KitType.GLOBAL);
        gkitCooldowns.put(kit.getYamlName(), cooldown);
    }
    public long getKitCooldown(EvolutionKit kit) {
        loadKitCooldowns(KitType.EVOLUTION);
        return vkitCooldowns.getOrDefault(kit.getYamlName(), 0l);
    }
    public void setKitCooldown(EvolutionKit kit, long cooldown) {
        loadKitCooldowns(KitType.EVOLUTION);
        vkitCooldowns.put(kit.getYamlName(), cooldown);
    }
    public long getKitCooldown(MasteryKit kit) {
        loadKitCooldowns(KitType.MASTERY);
        return mkitCooldowns.getOrDefault(kit.getYamlName(), 0l);
    }
    public void addKitCooldown(Object kit, long expiration) {
        final GlobalKit g = kit instanceof GlobalKit ? (GlobalKit) kit : null;
        final EvolutionKit v = g == null && kit instanceof EvolutionKit ? (EvolutionKit) kit : null;
        final MasteryKit m = v == null && kit instanceof MasteryKit ? (MasteryKit) kit : null;
        if(g == null && v == null && m == null) return;
        final boolean gkit = g != null, vkit = v != null;
        final KitType type = gkit ? KitType.GLOBAL : vkit ? KitType.EVOLUTION : KitType.MASTERY;
        loadKitCooldowns(type);
        (gkit ? gkitCooldowns : vkit ? vkitCooldowns : mkitCooldowns).put(gkit ? g.getYamlName() : vkit ? v.getYamlName() : m.getYamlName(), expiration);
    }


    private void loadOwnedMonthlyCrates() {
        if(ownedMonthlyCrates == null) {
            ownedMonthlyCrates = yml.getStringList("owned monthly crates");
        }
    }
    private void loadClaimedMonthlyCrates() {
        if(claimedMonthlyCrates == null) {
            claimedMonthlyCrates = yml.getStringList("claimed monthly crates");
        }
    }
    public List<String> getMonthlyCrates() {
        loadOwnedMonthlyCrates();
        return ownedMonthlyCrates;
    }
    public List<String> getClaimedMonthlyCrates() {
        loadClaimedMonthlyCrates();
        return claimedMonthlyCrates;
    }

    private void loadFactionUpgrades() {
        if(fadditions != null) {
            final String F = FactionsAPI.getFactionsAPI().getFaction(getOfflinePlayer());
            if(F != null && !factionUpgrades.containsKey(F)) {
                final HashMap<AbstractFactionUpgrade, Integer> upgrades = new HashMap<>();
                final ConfigurationSection c = fadditions.getConfigurationSection(F);
                if(c != null) {
                    for(String s : c.getKeys(false)) {
                        upgrades.put(AbstractFactionUpgrade.upgrades.getOrDefault(s, null), fadditions.getInt("factions." + F + "." + s));
                    }
                }
                factionUpgrades.put(F, upgrades);
            }
        }
    }


    private void loadUnclaimedLootboxes() {
        if(unclaimedLootboxes == null) {
            unclaimedLootboxes = new HashMap<>();
            final ConfigurationSection a = yml.getConfigurationSection("unclaimed lootboxes");
            if(a != null) {
                for(String s : a.getKeys(false)) {
                    unclaimedLootboxes.put(s, yml.getInt("unclaimed lootboxes." + s));
                }
            }
        }
    }
    public HashMap<String, Integer> getUnclaimedLootboxes() {
        loadUnclaimedLootboxes();
        return unclaimedLootboxes;
    }


    private void loadQuests() {
        final PlayerQuests QQ = PlayerQuests.getPlayerQuests();
        if(QQ.isEnabled() && quests == null) {
            quests = new HashMap<>();
            questTasks.put(uuid, new ArrayList<>());
            final ConfigurationSection c = yml.getConfigurationSection("quests");
            final boolean isEnabled = getPlugin.isEnabled();
            if(c != null) {
                final long time = System.currentTimeMillis();
                final BukkitScheduler scheduler = api.scheduler;
                final PluginManager pm = api.pluginmanager;
                for(String s : c.getKeys(false)) {
                    final PlayerQuest q = PlayerQuest.enabled.get(s);
                    final String[] b = yml.getString("quests." + s).split(";");
                    final ActivePlayerQuest a = new ActivePlayerQuest(Long.parseLong(b[0]), q, Double.parseDouble(b[1]), Boolean.parseBoolean(b[2]), Boolean.parseBoolean(b[3]));
                    if(!a.isExpired()) {
                        quests.put(q, a);
                        if(isEnabled) startExpire(time, scheduler, pm, q, a);
                    }
                }
            }
            int permfor = 0;
            final int max = QQ.questSlots.size();
            final Player p = Bukkit.getPlayer(uuid);
            for(int i = 1; i <= max; i++) {
                if(p.hasPermission("RandomPackage.playerquests." + i)) {
                    permfor = i;
                }
            }
            if(quests.size() != max) {
                final long time = System.currentTimeMillis();
                final Random random = new Random();
                final HashMap<String, PlayerQuest> pq = new HashMap<>(PlayerQuest.enabled);
                for(ActivePlayerQuest R : quests.values()) pq.remove(R.getQuest().getName());
                for(int i = 1; i <= permfor && quests.size() <= permfor-1; i++) {
                    loadNewQuest(time, random);
                }
            }
        }
    }
    private void loadNewQuest(long time, Random random) {
        final HashMap<String, PlayerQuest> pq = new HashMap<>(PlayerQuest.enabled);
        for(ActivePlayerQuest R : quests.values()) pq.remove(R.getQuest().getYamlName());
        final int pqs = pq.size();
        final BukkitScheduler s = api.scheduler;
        final PluginManager pm = api.pluginmanager;
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
    public void setQuests(HashMap<PlayerQuest, ActivePlayerQuest> quests) {
        this.quests = quests;
        if(quests == null) {
            yml.set("quests", null);
            save();
        }
    }


    public static HashMap<AbstractFactionUpgrade, Integer> getFactionUpgrades(OfflinePlayer player) {
        final String F = FactionsAPI.getFactionsAPI().getFaction(player);
        if(F != null) {
            if(!factionUpgrades.containsKey(F)) factionUpgrades.put(F, new HashMap<>());
            return factionUpgrades.get(F);
        }
        return new HashMap<>();
    }
    public static HashMap<String, HashMap<AbstractFactionUpgrade, Integer>> getFactionUpgrades() {
        return factionUpgrades;
    }

    public static void loadAllPlayerData() {
        try {
            for(File f : new File(folder).listFiles()) {
                RPPlayer.get(UUID.fromString(f.getName().split("\\.yml")[0])).load();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void unloadAllPlayerData() {
        try {
            for(File f : new File(folder).listFiles()) {
                RPPlayer.get(UUID.fromString(f.getName().split("\\.yml")[0])).unload();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
