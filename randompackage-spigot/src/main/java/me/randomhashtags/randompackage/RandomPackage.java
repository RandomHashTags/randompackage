package me.randomhashtags.randompackage;

import me.randomhashtags.randompackage.api.*;
import me.randomhashtags.randompackage.api.addon.*;
import me.randomhashtags.randompackage.api.dev.InventoryPets;
import me.randomhashtags.randompackage.attributesys.EventAttributes;
import me.randomhashtags.randompackage.dev.LastManStanding;
import me.randomhashtags.randompackage.dev.Outposts;
import me.randomhashtags.randompackage.dev.SlotBot;
import me.randomhashtags.randompackage.dev.SpawnerStacking;
import me.randomhashtags.randompackage.api.addon.Alchemist;
import me.randomhashtags.randompackage.api.addon.Enchanter;
import me.randomhashtags.randompackage.api.addon.Tinkerer;
import me.randomhashtags.randompackage.dev.duels.Duels;
import me.randomhashtags.randompackage.dev.dungeons.Dungeons;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.economy.Vault;
import me.randomhashtags.randompackage.supported.standalone.ClipPAPI;
import me.randomhashtags.randompackage.universal.UVersion;
import me.randomhashtags.randompackage.util.CommandManager;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.listener.RPEvents;
import me.randomhashtags.randompackage.util.obj.Backup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;

import static me.randomhashtags.randompackage.RandomPackageAPI.spawnerchance;

public final class RandomPackage extends JavaPlugin {
    public static RandomPackage getPlugin;

    public FileConfiguration config;

    private RandomPackageAPI api;
    private RPEvents rpevents;

    public static String spawner;
    public static Plugin spawnerPlugin, mcmmo;
    public boolean placeholderapi = false;

    private BukkitScheduler scheduler;
    private PluginManager pluginmanager;

    public void onEnable() {
        getPlugin = this;
        enable();
    }
    public void onDisable() {
        disable();
    }

    private void enable() {
        scheduler = Bukkit.getScheduler();
        pluginmanager = Bukkit.getPluginManager();
        checkForUpdate();
        checkFiles();
        loadSoftDepends();

        api = RandomPackageAPI.api;
        rpevents = RPEvents.getRPEvents();

        final Vault vault = Vault.getVault();
        vault.setupEconomy();
        vault.setupPermissions();

        api.enable();
        getCommand("randompackage").setExecutor(api);
        rpevents.enable();
        RegionalAPI.getRegionalAPI().setup(this);

        EventAttributes.loadEventAttributes();

        final CommandManager cmd = CommandManager.getCommandManager();

        cmd.load(SecondaryEvents.getSecondaryEvents(), Arrays.asList("balance", "bless", "combine", "confirm", "roll", "withdraw", "xpbottle"), isTrue("balance", "bless", "combine", "roll", "withdraw", "xpbottle"));
        cmd.loadCustom(AuctionHouse.getAuctionHouse(), getHash("auctionhouse", "auction house"), isTrue("auction house"));
        cmd.loadCustom(Boosters.getBoosters(), null, isTrue("boosters"));
        cmd.loadCustom(ChatEvents.getChatEvents(), getHash("brag", "chat cmds.brag"), isTrue("chat cmds.brag", "chat cmds.item"));
        cmd.load(CoinFlip.getCoinFlip(), Arrays.asList("coinflip"), isTrue("coinflip"));
        cmd.loadCustom(CollectionFilter.getCollectionFilter(), getHash("collectionfilter", "collection filter"), isTrue("collection filter.enabled"));
        cmd.load(Conquest.getConquest(), Arrays.asList("conquest"), isTrue("conquest"));
        cmd.load(CustomArmor.getCustomArmor(), null, isTrue("custom armor"));
        cmd.loadCustom(CustomBosses.getCustomBosses(), null, isTrue("custom bosses"));

        cmd.load(Alchemist.getAlchemist(), Arrays.asList("alchemist"), isTrue("alchemist"));
        cmd.load(Tinkerer.getTinkerer(), Arrays.asList("tinkerer"), isTrue("tinkerer"));

        cmd.loadCustom(CustomEnchants.getCustomEnchants(), getHash("disabledenchants", "disabled enchants", "enchants", "enchants"), isTrue("disabled enchants", "enchants"));
        cmd.load(BlackScrolls.getBlackScrolls(), null, isTrue("custom enchants.black scrolls", true));
        cmd.load(EnchantmentOrbs.getEnchantmentOrbs(), null, isTrue("custom enchants.enchantment orbs", true));
        cmd.load(Fireballs.getFireballs(), null, isTrue("custom enchants.fireballs", true));
        cmd.load(RandomizationScrolls.getRandomizationScrolls(), null, isTrue("custom enchants.randomization scrolls", true));
        cmd.load(RarityGems.getRarityGems(), null, isTrue("custom enchants.rarity gems", true));
        cmd.load(SoulTrackers.getSoulTrackers(), Arrays.asList("splitsouls"), isTrue("custom enchants.soul trackers", true) || isTrue("splitsouls"));
        cmd.load(TransmogScrolls.getTransmogScrolls(), null, isTrue("custom enchants.transmog scrolls", true));
        cmd.load(WhiteScrolls.getWhiteScrolls(), null, isTrue("custom enchants.white scrolls", true));

        cmd.loadCustom(CustomExplosions.getCustomExplosions(), null, isTrue("custom creepers", "custom tnt"));
        cmd.loadCustom(Duels.getDuels(), getHash("duel", "duels"), isTrue("duels"));
        cmd.loadCustom(Dungeons.getDungeons(), getHash("dungeon", "dungeons"), isTrue("dungeons"));
        cmd.load(Envoy.getEnvoy(), Arrays.asList("envoy"), isTrue("envoy"));
        cmd.loadCustom(FactionUpgrades.getFactionUpgrades(), null, isTrue("faction upgrades"));
        cmd.loadCustom(FatBuckets.getFatBuckets(), null, isTrue("fat buckets"));
        cmd.load(Fund.getFund(), Arrays.asList("fund"), isTrue("fund"));
        cmd.loadCustom(GlobalChallenges.getChallenges(), getHash("challenge", "global challenges"), isTrue("global challenges"));
        cmd.load(Homes.getHomes(), Arrays.asList("home", "sethome"), isTrue("home", "sethome"));
        cmd.loadCustom(ItemFilter.getItemFilter(), getHash("filter", "item filter"), isTrue("item filter"));
        cmd.load(Jackpot.getJackpot(), Arrays.asList("jackpot"), isTrue("jackpot"));

        cmd.loadCustom(KitsEvolution.getKitsEvolution(), getHash("vkit", "vkits"), isTrue("vkits"));
        cmd.loadCustom(KitsGlobal.getKitsGlobal(), getHash("gkit", "gkits"), isTrue("gkits"));
        cmd.loadCustom(KitsMastery.getKitsMastery(), getHash("mkit", "mkits"), isTrue("mkits"));

        cmd.load(KOTH.getKOTH(), Arrays.asList("kingofthehill"), isTrue("kingofthehill"));
        cmd.loadCustom(LastManStanding.getLastManStanding(), getHash("lastmanstanding", "last man standing"), isTrue("last man standing"));
        cmd.load(Masks.getMasks(), null, isTrue("masks"));
        cmd.load(MobStacker.getMobStacker(), null, isTrue("mob stacker"));
        cmd.loadCustom(Outposts.getOutposts(), getHash("outpost", "outposts"), isTrue("outposts"));
        cmd.load(InventoryPets.getInventoryPets(), null, isTrue("inventory pets"));
        cmd.load(Trinkets.getTrinkets(), null, isTrue("trinkets"));
        cmd.loadCustom(MonthlyCrates.getMonthlyCrates(), getHash("monthlycrate", "monthly crates"), isTrue("monthly crates"));
        cmd.load(ServerCrates.getServerCrates(), null, isTrue("server crates"));
        cmd.load(Titles.getTitles(), Arrays.asList("title"), isTrue("title"));
        cmd.load(Showcase.getShowcase(), Arrays.asList("showcase"), isTrue("showcase"));
        cmd.loadCustom(Lootboxes.getLootboxes(), getHash("lootbox", "lootboxes"), isTrue("lootboxes"));
        cmd.loadCustom(PlayerQuests.getPlayerQuests(), getHash("quest", "player quests"), isTrue("player quests"));
        cmd.load(Shop.getShop(), Arrays.asList("shop"), isTrue("shop"));
        cmd.loadCustom(SpawnerStacking.getSpawnerStacking(), null, isTrue("spawner stacking"));
        cmd.load(Trade.getTrade(), Arrays.asList("trade"), isTrue("trade"));
        cmd.load(Wild.getWild(), Arrays.asList("wild"), isTrue("wild"));
        cmd.loadCustom(WildPvP.getWildPvP(), getHash("wildpvp", "wild pvp"), isTrue("wild pvp"));

        RandomizedLoot.getRandomizedLoot().enable();
        cmd.loadCustom(SlotBot.getSlotBot(), getHash("slotbot", "slot bot"), isTrue("slot bot"));
        cmd.load(Enchanter.getEnchanter(), Arrays.asList("enchanter"), isTrue("enchanter"));

        final int interval = config.getInt("backup interval")*20*60;
        scheduler.scheduleSyncRepeatingTask(this, ()-> new Backup(), interval, interval);
    }
    private boolean isTrue(String path, boolean exact) {
        return config.getBoolean(path + (exact ? "" : ".enabled"));
    }
    private boolean isTrue(String...paths) {
        boolean enabled = false;
        for(String s : paths) {
            if(config.getBoolean(s + ".enabled")) {
                enabled = true;
                break;
            }
        }
        return enabled;
    }
    private HashMap<String, String> getHash(String...values) {
        final HashMap<String, String> a = new HashMap<>();
        for(int i = 0; i < values.length; i++) {
            if(i%2 == 1) {
                a.put(values[i-1], values[i]);
            }
        }
        return a;
    }

    private void checkFiles() {
        UVersion.getUVersion().save(null, "config.yml");
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
    }
    private void loadSoftDepends() {
        tryLoadingMCMMO();
        tryLoadingSpawner();
        if(isTrue("supported plugins.standalone.PlaceholderAPI", true) && pluginmanager.isPluginEnabled("PlaceholderAPI")) {
            placeholderapi = true;
            ClipPAPI.getPAPI();
        }
    }
    public void tryLoadingMCMMO() {
        if(isTrue("supported plugins.mechanics.MCMMO", true) && pluginmanager.isPluginEnabled("mcMMO")) {
            mcmmo = pluginmanager.getPlugin("mcMMO");
        }
    }
    public void tryLoadingSpawner() {
        final String ss = isTrue("supported plugins.mechanics.SilkSpawners", true) && pluginmanager.isPluginEnabled("SilkSpawners") ? "SilkSpawners" : null;
        final String es = isTrue("supported plugins.mechanics.EpicSpawners", true) && pluginmanager.isPluginEnabled("EpicSpawners") ? "EpicSpawners" + (pluginmanager.getPlugin("EpicSpawners").getDescription().getVersion().startsWith("5") ? "5" : "6") : null;
        final boolean epic = es != null;
        if(epic || ss != null) {
            spawnerPlugin = pluginmanager.getPlugin(epic ? "EpicSpawners" : "SilkSpawners");
            spawner = epic ? es : ss;
            final FileConfiguration c = spawnerPlugin.getConfig();
            spawnerchance = epic ? Integer.parseInt(c.getString("Spawner Drops.Chance On TNT Explosion").replace("%", "")): c.getInt("explosionDropChance");
        }
    }
    private void disable() {
        rpevents.disable();
        api.disable();
        EventAttributes.unloadEventAttributes();

        CommandManager.getCommandManager().disable();
        RPFeature.d();
        HandlerList.unregisterAll((Plugin) this);
        scheduler.cancelTasks(this);
    }

    public void checkForUpdate() {
        final int l = 20*60*30;
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            scheduler.runTaskAsynchronously(this, () -> {
                String msg = null;
                try {
                    final URLConnection con = new URL("https://api.spigotmc.org/legacy/update.php?resource=38501").openConnection();
                    final String v = getPlugin.getDescription().getVersion(), newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
                    final boolean canUpdate = !v.equals(newVersion);
                    if(canUpdate) {
                        msg = ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage] &eUpdate available! &aYour version: &f" + v + "&a. Latest version: &f" + newVersion);
                    }
                } catch (Exception e) {
                    msg = ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage] &cCould not check for updates due to being unable to connect to SpigotMC!");
                }
                if(msg != null) {
                    Bukkit.getConsoleSender().sendMessage(msg);
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        if(p.isOp()) p.sendMessage(msg);
                    }
                }
            });
        }, 0, l);
    }
    public void reload() {
        disable();
        enable();
    }
}
