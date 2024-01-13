package me.randomhashtags.randompackage;

import me.randomhashtags.randompackage.api.*;
import me.randomhashtags.randompackage.api.addon.*;
import me.randomhashtags.randompackage.api.dev.InventoryPets;
import me.randomhashtags.randompackage.api.dev.LastManStanding;
import me.randomhashtags.randompackage.attributesys.EventAttributes;
import me.randomhashtags.randompackage.dev.Outposts;
import me.randomhashtags.randompackage.dev.SpawnerStacking;
import me.randomhashtags.randompackage.dev.duels.Duels;
import me.randomhashtags.randompackage.dev.dungeons.Dungeons;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.economy.Vault;
import me.randomhashtags.randompackage.supported.standalone.ClipPAPI;
import me.randomhashtags.randompackage.util.CommandManager;
import me.randomhashtags.randompackage.util.Language;
import me.randomhashtags.randompackage.util.listener.RPEventsSpigot;
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
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.randomhashtags.randompackage.RandomPackageAPI.SPAWNER_CHANCE;

public final class RandomPackage extends JavaPlugin {
    public static RandomPackage INSTANCE;

    public FileConfiguration config;


    private RandomPackageAPI api;
    private RPEventsSpigot rpevents;
    public static String SPAWNER_PLUGIN_NAME;
    public static Language LOCALIZATION;
    public static Plugin SPAWNER_PLUGIN, MCMMO;
    public boolean placeholder_api = false;

    @Override
    public void onEnable() {
        INSTANCE = this;
        enable();
    }
    @Override
    public void onDisable() {
        disable();
    }

    private void enable() {
        check_for_update();
        check_files();
        load_soft_dependencies();

        api = RandomPackageAPI.INSTANCE;
        rpevents = RPEventsSpigot.INSTANCE;

        final Vault vault = Vault.INSTANCE;
        vault.setupEconomy();
        vault.setupPermissions();

        api.enable();
        getCommand("randompackage").setExecutor(api);
        rpevents.enable();
        RegionalAPI.INSTANCE.setup();

        EventAttributes.loadEventAttributes();

        final CommandManager cmd = CommandManager.INSTANCE;

        cmd.load(ArmorSockets.INSTANCE, null, isTrue("armor sockets"));
        cmd.load(Combine.INSTANCE, List.of("combine"), isTrue("combine"));
        cmd.load(Xpbottle.INSTANCE, List.of("xpbottle"), isTrue("xpbottle"));
        cmd.load(SecondaryEvents.INSTANCE, List.of("balance", "bless", "confirm", "roll", "withdraw"), isTrue("balance", "bless", "roll", "withdraw"));
        cmd.loadCustom(AuctionHouse.INSTANCE, getHash("auctionhouse", "auction house"), isTrue("auction house"));
        cmd.loadCustom(Boosters.INSTANCE, null, isTrue("boosters"));
        cmd.loadCustom(ChatEvents.INSTANCE, getHash("brag", "chat cmds.brag"), isTrue("chat cmds.brag", "chat cmds.item"));
        cmd.load(CoinFlip.INSTANCE, List.of("coinflip"), isTrue("coinflip"));
        cmd.loadCustom(CollectionFilter.INSTANCE, getHash("collectionfilter", "collection filter"), isTrue("collection filter"));
        cmd.load(Conquest.INSTANCE, List.of("conquest"), isTrue("conquest"));
        cmd.load(CustomArmor.INSTANCE, null, isTrue("custom armor"));
        cmd.loadCustom(CustomBosses.INSTANCE, null, isTrue("custom bosses"));

        cmd.load(Alchemist.INSTANCE, List.of("alchemist"), isTrue("alchemist"));
        cmd.load(Tinkerer.INSTANCE, List.of("tinkerer"), isTrue("tinkerer"));

        cmd.loadCustom(CustomEnchants.INSTANCE, getHash("disabledenchants", "disabled enchants", "enchants", "enchants"), isTrue("disabled enchants", "enchants"));
        cmd.load(EnchantmentOrbs.INSTANCE, null, isTrue("custom enchants.enchantment orbs", true));
        cmd.load(Fireballs.INSTANCE, null, isTrue("custom enchants.fireballs", true));
        cmd.load(RarityGems.INSTANCE, null, isTrue("custom enchants.rarity gems", true));
        cmd.load(SoulTrackers.INSTANCE, List.of("splitsouls"), isTrue("custom enchants.soul trackers", true) || isTrue("splitsouls"));

        final Set<Boolean> scrolls = new HashSet<>() {{
            add(config.getBoolean("custom enchants.black scrolls"));
            add(config.getBoolean("custom enchants.randomization scrolls"));
            add(config.getBoolean("custom enchants.transmog scrolls"));
            add(config.getBoolean("custom enchants.white scrolls"));
            add(config.getBoolean("custom enchants.holy scrolls"));
        }};
        cmd.load(Scrolls.INSTANCE, null, scrolls.contains(true));

        cmd.loadCustom(CustomExplosions.INSTANCE, null, isTrue("custom creepers", "custom tnt"));
        cmd.loadCustom(Duels.INSTANCE, getHash("duel", "duels"), isTrue("duels"));
        cmd.loadCustom(Dungeons.INSTANCE, getHash("dungeon", "dungeons"), isTrue("dungeons"));
        cmd.load(Envoy.INSTANCE, List.of("envoy"), isTrue("envoy"));
        cmd.loadCustom(FactionUpgrades.INSTANCE, null, isTrue("faction upgrades"));
        cmd.loadCustom(FatBuckets.INSTANCE, null, isTrue("fat buckets"));
        cmd.load(Fund.INSTANCE, List.of("fund"), isTrue("fund"));
        cmd.loadCustom(GlobalChallenges.INSTANCE, getHash("challenge", "global challenges"), isTrue("global challenges"));
        cmd.load(Homes.INSTANCE, List.of("home", "sethome"), isTrue("home", "sethome"));
        cmd.loadCustom(ItemFilter.INSTANCE, getHash("filter", "item filter"), isTrue("item filter"));
        cmd.load(ItemSkins.INSTANCE, null, isTrue("item skins"));
        cmd.load(Jackpot.INSTANCE, List.of("jackpot"), isTrue("jackpot"));

        cmd.loadCustom(KitsEvolution.getKitsEvolution(), getHash("vkit", "vkits"), isTrue("vkits"));
        cmd.loadCustom(KitsGlobal.getKitsGlobal(), getHash("gkit", "gkits"), isTrue("gkits"));
        cmd.loadCustom(KitsMastery.getKitsMastery(), getHash("mkit", "mkits"), isTrue("mkits"));

        cmd.load(KingOfTheHill.INSTANCE, List.of("kingofthehill"), isTrue("kingofthehill"));
        cmd.loadCustom(LastManStanding.INSTANCE, getHash("lastmanstanding", "last man standing"), isTrue("last man standing"));
        cmd.load(Masks.INSTANCE, null, isTrue("masks"));
        cmd.load(MobStacker.INSTANCE, null, isTrue("mob stacker"));
        cmd.loadCustom(Outposts.INSTANCE, getHash("outpost", "outposts"), isTrue("outposts"));
        cmd.load(InventoryPets.INSTANCE, null, isTrue("inventory pets"));
        cmd.load(Trinkets.INSTANCE, null, isTrue("trinkets"));
        cmd.loadCustom(MonthlyCrates.INSTANCE, getHash("monthlycrate", "monthly crates"), isTrue("monthly crates"));
        cmd.load(ServerCrates.INSTANCE, null, isTrue("server crates"));
        cmd.load(Titles.INSTANCE, List.of("title"), isTrue("title"));
        cmd.load(Showcase.INSTANCE, List.of("showcase"), isTrue("showcase"));
        cmd.loadCustom(PlayerQuests.INSTANCE, getHash("quest", "player quests"), isTrue("player quests"));
        cmd.loadCustom(Lootboxes.INSTANCE, getHash("lootbox", "lootboxes"), isTrue("lootboxes"));

        RandomizedLoot.INSTANCE.enable();
        cmd.loadCustom(SlotBot.INSTANCE, getHash("slotbot", "slot bot"), isTrue("slot bot"));
        cmd.load(Enchanter.INSTANCE, List.of("enchanter"), isTrue("enchanter"));

        cmd.load(Shop.INSTANCE, List.of("shop"), isTrue("shop"));
        cmd.loadCustom(SpawnerStacking.INSTANCE, null, isTrue("spawner stacking"));
        cmd.load(Trade.INSTANCE, List.of("trade"), isTrue("trade"));
        cmd.load(Wild.INSTANCE, List.of("wild"), isTrue("wild"));
        cmd.loadCustom(WildPvP.INSTANCE, getHash("wildpvp", "wild pvp"), isTrue("wild pvp"));

        final int interval = config.getInt("backup interval")*20*60;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ()-> new Backup(), interval, interval);
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
    @NotNull
    private HashMap<String, String> getHash(String...values) {
        final HashMap<String, String> a = new HashMap<>();
        for(int i = 0; i < values.length; i++) {
            if(i % 2 == 1) {
                a.put(values[i-1], values[i]);
            }
        }
        return a;
    }

    private void check_files() {
        RandomPackageAPI.INSTANCE.save(null, "config.yml");
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        LOCALIZATION = Language.ENGLISH;
    }
    private void load_soft_dependencies() {
        try_loading_mcmmo();
        try_loading_spawner();
        if(isTrue("supported plugins.standalone.PlaceholderAPI", true) && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholder_api = true;
            ClipPAPI.getPAPI();
        }
    }
    public void try_loading_mcmmo() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        if(isTrue("supported plugins.mechanics.MCMMO", true) && pluginManager.isPluginEnabled("mcMMO")) {
            MCMMO = pluginManager.getPlugin("mcMMO");
        }
    }
    public void try_loading_spawner() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        final String ss = isTrue("supported plugins.mechanics.SilkSpawners", true) && pluginManager.isPluginEnabled("SilkSpawners") ? "SilkSpawners" : null;
        final String es = isTrue("supported plugins.mechanics.EpicSpawners", true) && pluginManager.isPluginEnabled("EpicSpawners") ? "EpicSpawners" + (pluginManager.getPlugin("EpicSpawners").getDescription().getVersion().startsWith("5") ? "5" : "6") : null;
        final boolean epic = es != null;
        if(epic || ss != null) {
            SPAWNER_PLUGIN = pluginManager.getPlugin(epic ? "EpicSpawners" : "SilkSpawners");
            SPAWNER_PLUGIN_NAME = epic ? es : ss;
            final FileConfiguration config = SPAWNER_PLUGIN.getConfig();
            final String targetString = epic ? config.getString("Spawner Drops.Chance On TNT Explosion") : null;
            SPAWNER_CHANCE = epic ? targetString != null ? Integer.parseInt(targetString.replace("%", "")) : 0 : config.getInt("explosionDropChance");
        }
    }

    private void disable() {
        rpevents.disable();
        api.disable();
        EventAttributes.unloadEventAttributes();

        CommandManager.INSTANCE.disable();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void check_for_update() {
        final int updateCheckInterval = 20 * 60 * 30;
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        final String version = getDescription().getVersion();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            scheduler.runTaskAsynchronously(this, () -> {
                String msg = null;
                try {
                    final URLConnection con = new URL("https://api.spigotmc.org/legacy/update.php?resource=38501").openConnection();
                    final String newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
                    final boolean canUpdate = !version.equals(newVersion);
                    if(canUpdate) {
                        msg = ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage] &eUpdate available! &aYour version: &f" + version + "&a. Latest version: &f" + newVersion);
                    }
                } catch (Exception e) {
                    msg = ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage] &cCould not check for updates due to being unable to connect to SpigotMC!");
                }
                if(msg != null) {
                    Bukkit.getConsoleSender().sendMessage(msg);
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        if(p.isOp()) {
                            p.sendMessage(msg);
                        }
                    }
                }
            });
        }, 0, updateCheckInterval);
    }
    public void reload() {
        disable();
        enable();
    }
}
