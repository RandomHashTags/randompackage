package me.randomhashtags.randompackage;

import me.randomhashtags.randompackage.api.*;
import me.randomhashtags.randompackage.api.CollectionFilter;
import me.randomhashtags.randompackage.api.Trade;
import me.randomhashtags.randompackage.api.WildPvP;
import me.randomhashtags.randompackage.api.events.PlayerArmorEvent;
import me.randomhashtags.randompackage.api.PlayerQuests;
import me.randomhashtags.randompackage.api.nearFinished.FactionUpgrades;
import me.randomhashtags.randompackage.api.nearFinished.Outposts;
import me.randomhashtags.randompackage.api.unfinished.*;
import me.randomhashtags.randompackage.utils.RPEvents;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.Updater;
import me.randomhashtags.randompackage.utils.supported.VaultAPI;
import me.randomhashtags.randompackage.utils.supported.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackageAPI.spawnerchance;
import static me.randomhashtags.randompackage.utils.RPFeature.givedp;
import static me.randomhashtags.randompackage.utils.RPFeature.givedpCategories;

public final class RandomPackage extends JavaPlugin implements Listener {
    public static RandomPackage getPlugin;

    private FileConfiguration config;
    private String v;

    private SimpleCommandMap commandMap;
    private HashMap<String, Command> knownCommands;
    private HashMap<String, PluginCommand> Y = new HashMap<>(), originalCommands = new HashMap<>();

    private AuctionHouse auctionhouse;
    private ChatEvents chatevents;
    private CoinFlip coinflip;
    private CollectionFilter collectionfilter;
    private Conquest conquest;
    private CustomArmor customarmor;
    private CustomBosses custombosses;
    private CustomEnchants customenchants;
    private CustomExplosions customexplosions;
    private Duels duels;
    private Envoy envoy;
    private FactionUpgrades factionupgrades;
    private Fund fund;
    private GlobalChallenges globalchallenges;
    private Homes homes;
    private ItemFilter itemfilter;
    private Jackpot jackpot;
    private Kits kits;
    private KOTH koth;
    private LastManStanding lastmanstanding;
    private Lootboxes lootboxes;
    private Masks masks;
    private MobStacker mobstacker;
    private MonthlyCrates monthlycrates;
    private Outposts outposts;
    private Pets pets;
    private PlayerQuests playerquests;
    private ServerCrates servercrates;
    private Shop shop;
    private Showcase showcase;
    private Titles titles;
    private Trade trade;
    private Trinkets trinkets;
    private WildPvP wildpvp;

    private RandomPackageAPI api;
    private RPEvents rpevents;
    private SecondaryEvents secondaryevents;
    private VaultAPI vapi;

    public static String spawner;
    public static Plugin spawnerPlugin, mcmmo;

    private PluginManager pm;

    public void onEnable() {
        getPlugin = this;
        enable();
    }
    public void onDisable() {
        disable();
    }

    private void enable() {
        v = Bukkit.getVersion();
        pm = Bukkit.getPluginManager();
        pm.registerEvents(this, this);
        checkForUpdate();
        checkFiles();
        loadSoftDepends();

        try {
            commandMap = (SimpleCommandMap) getPrivateField(getServer().getPluginManager(), "commandMap");
            knownCommands = (HashMap<String, Command>) getPrivateField(commandMap, "knownCommands");
            Y = new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(String s : getDescription().getCommands().keySet()) originalCommands.put(s, getCommand(s));
        final Collection<String> keys = knownCommands.keySet();
        for(String s : keys) {
            final Command cmd = knownCommands.get(s);
            final PluginCommand pc = cmd instanceof PluginCommand ? (PluginCommand) cmd : null;
            if(pc != null) Y.put(pc.getPlugin().getName() + ":" + s, pc);
        }

        api = RandomPackageAPI.api;
        rpevents = RPEvents.getRPEvents();

        vapi = VaultAPI.getVaultAPI();
        vapi.setupEconomy();

        api.enable();
        getCommand("randompackage").setExecutor(api);
        try {
            addAliases("randompackage");
        } catch (Exception e) {
            e.printStackTrace();
        }
        rpevents.enable();
        secondaryevents = SecondaryEvents.getSecondaryEvents();
        tryLoading(Feature.SECONDARY_EVENTS);

        auctionhouse = AuctionHouse.getAuctionHouse();
        tryLoading(Feature.AUCTION_HOUSE);

        chatevents = ChatEvents.getChatEvents();
        tryLoading(Feature.CHAT_BRAG);

        coinflip = CoinFlip.getCoinFlip();
        tryLoading(Feature.COINFLIP);

        collectionfilter = CollectionFilter.getCollectionFilter();
        tryLoading(Feature.COLLECTION_FILTER);

        conquest = Conquest.getConquest();
        tryLoading(Feature.CONQUEST);

        customarmor = CustomArmor.getCustomArmor();
        tryLoading(Feature.CUSTOM_ARMOR);

        custombosses = CustomBosses.getCustomBosses();
        tryLoading(Feature.CUSTOM_BOSSES);

        customenchants = CustomEnchants.getCustomEnchants();
        tryLoading(Feature.CUSTOM_ENCHANTS);

        customexplosions = CustomExplosions.getCustomExplosions();
        tryLoading(Feature.CUSTOM_CREEPERS);
        tryLoading(Feature.CUSTOM_TNT);

        duels = Duels.getDuels();
        tryLoading(Feature.DUELS);

        envoy = Envoy.getEnvoy();
        tryLoading(Feature.ENVOY);

        factionupgrades = FactionUpgrades.getFactionUpgrades();
        tryLoading(Feature.FACTION_UPGRADES);

        fund = Fund.getFund();
        tryLoading(Feature.FUND);

        globalchallenges = GlobalChallenges.getChallenges();
        tryLoading(Feature.GLOBAL_CHALLENGES);

        homes = Homes.getHomes();
        tryLoading(Feature.HOMES);

        itemfilter = ItemFilter.getItemFilter();
        tryLoading(Feature.ITEM_FILTER);

        jackpot = Jackpot.getJackpot();
        tryLoading(Feature.JACKPOT);

        kits = Kits.getKits();
        tryLoading(Feature.KITS_EVOLUTION);
        tryLoading(Feature.KITS_GLOBAL);
        tryLoading(Feature.KITS_MASTERY);

        koth = KOTH.getKOTH();
        tryLoading(Feature.KOTH);

        lastmanstanding = LastManStanding.getLastManStanding();
        tryLoading(Feature.LAST_MAN_STANDING);

        masks = Masks.getMasks();
        tryLoading(Feature.MASKS);

        mobstacker = MobStacker.getMobStacker();
        tryLoading(Feature.MOB_STACKER);

        outposts = Outposts.getOutposts();
        tryLoading(Feature.OUTPOSTS);

        pets = Pets.getPets();
        tryLoading(Feature.PETS);

        trinkets = Trinkets.getTrinkets();
        tryLoading(Feature.TRINKETS);

        monthlycrates = MonthlyCrates.getMonthlyCrates();
        tryLoading(Feature.MONTHLY_CRATES);

        servercrates = ServerCrates.getServerCrates();
        tryLoading(Feature.SERVER_CRATES);

        titles = Titles.getTitles();
        tryLoading(Feature.TITLES);

        lootboxes = Lootboxes.getLootboxes();
        tryLoading(Feature.LOOTBOXES);

        shop = Shop.getShop();
        tryLoading(Feature.SHOP);

        showcase = Showcase.getShowcase();
        tryLoading(Feature.SHOWCASE);

        playerquests = PlayerQuests.getPlayerQuests();
        tryLoading(Feature.PLAYER_QUESTS);

        trade = Trade.getTrade();
        tryLoading(Feature.TRADE);

        wildpvp = WildPvP.getWildPvP();
        tryLoading(Feature.WILD_PVP);

        if(pm.isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPI();
        }
    }
    private void checkFiles() {
        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
    }
    private void loadSoftDepends() {
        final PluginManager pm = Bukkit.getPluginManager();
        mcmmo = pm.isPluginEnabled("mcMMO") ? pm.getPlugin("mcMMO") : null;
        tryLoadingSpawner();
    }
    public void tryLoadingSpawner() {
        final String es = pm.isPluginEnabled("EpicSpawners") ? "EpicSpawners" + (pm.getPlugin("EpicSpawners").getDescription().getVersion().startsWith("5") ? "5" : "6") : null;
        final String ss = pm.isPluginEnabled("SilkSpawners") ? "SilkSpawners" : null;
        spawnerPlugin = es != null || ss != null ? pm.getPlugin(es != null ? "EpicSpawners" : "SilkSpawners") : null;
        spawner = es != null ? es : ss != null ? ss : null;
        spawnerchance = es != null ? Integer.parseInt(spawnerPlugin.getConfig().getString("Spawner Drops.Chance On TNT Explosion").replace("%", "")): ss != null ? spawnerPlugin.getConfig().getInt("explosionDropChance") : 0;
    }
    private void disable() {
        rpevents.disable();

        for(Feature f : Feature.values()) {
            tryDisabling(f);
        }

        secondaryevents.disable();
        api.disable();
        givedp = null;
        givedpCategories = null;
        HandlerList.unregisterAll((Listener) this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void checkForUpdate() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> Updater.getUpdater().checkForUpdate());
    }
    public void reload() {
        disable();
        enable();
    }
    enum Feature {
        AUCTION_HOUSE, CHAT_BRAG, COINFLIP, COLLECTION_FILTER, CONQUEST, CUSTOM_ARMOR, CUSTOM_BOSSES, CUSTOM_ENCHANTS, CUSTOM_CREEPERS, CUSTOM_TNT,
        DUELS, ENVOY, FACTION_UPGRADES, FUND, GLOBAL_CHALLENGES,
        HOMES, ITEM_FILTER, JACKPOT,
        KITS_EVOLUTION, KITS_GLOBAL, KITS_MASTERY,
        KOTH, LAST_MAN_STANDING, LOOTBOXES, MASKS, MOB_STACKER, MONTHLY_CRATES, OUTPOSTS, PLAYER_QUESTS, PETS,
        SECONDARY_EVENTS, SERVER_CRATES, SHOP, SHOWCASE, TITLES, TRADE, TRINKETS, WILD_PVP,
    }
    public void tryLoading(Feature f) {
        try {
            tryloading(f);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage &cERROR&6] &c&lError trying to load feature:&r &f" + f.name()));
            e.printStackTrace();
        }
    }
    public void tryDisabling(Feature f) {
        final RPFeature a = getfeature(f);
        if(a != null) {
            try {
                a.disable();
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage &cERROR&6] &c&lError trying to disable feature:&r &f" + f.name()));
                e.printStackTrace();
            }
        }
    }
    private RPFeature getfeature(Feature f) {
        switch(f) {
            case AUCTION_HOUSE: return auctionhouse;
            case CHAT_BRAG: return chatevents;
            case COINFLIP: return coinflip;
            case CONQUEST: return conquest;
            case COLLECTION_FILTER: return collectionfilter;
            case CUSTOM_ARMOR: return customarmor;
            case CUSTOM_BOSSES: return custombosses;
            case CUSTOM_ENCHANTS: return customenchants;
            case CUSTOM_CREEPERS:
            case CUSTOM_TNT: return customexplosions;
            case DUELS: return duels;
            case ENVOY: return envoy;
            case FACTION_UPGRADES: return factionupgrades;
            case FUND: return fund;
            case GLOBAL_CHALLENGES: return globalchallenges;
            case HOMES: return homes;
            case ITEM_FILTER: return itemfilter;
            case JACKPOT: return jackpot;
            case KITS_EVOLUTION:
            case KITS_GLOBAL:
            case KITS_MASTERY: return kits;
            case KOTH: return koth;
            case LAST_MAN_STANDING: return lastmanstanding;
            case LOOTBOXES: return lootboxes;
            case MASKS: return masks;
            case MOB_STACKER: return mobstacker;
            case MONTHLY_CRATES: return monthlycrates;
            case OUTPOSTS: return outposts;
            case PETS: return pets;
            case PLAYER_QUESTS: return playerquests;
            case SERVER_CRATES: return servercrates;
            case SHOP: return shop;
            case SHOWCASE: return showcase;
            case TITLES: return titles;
            case TRADE: return trade;
            case TRINKETS: return trinkets;
            case WILD_PVP: return wildpvp;
        }
        return null;
    }
    private void tryloading(Feature f) throws Exception {
        boolean enabled = false;
        final HashMap<String, String> ali = new HashMap<>();
        final List<String> cmds = new ArrayList<>();
        CommandExecutor ce = null;
        if(f.equals(Feature.AUCTION_HOUSE)) {
            enabled = config.getBoolean("auction house.enabled");
            ce = auctionhouse;
            cmds.add("auctionhouse");
            ali.put("auctionhouse", "auction house");
            if(enabled) {
                auctionhouse.enable();
            }
        } else if(f.equals(Feature.CHAT_BRAG)) {
            enabled = config.getBoolean("chat cmds.brag.enabled");
            ce = chatevents;
            cmds.add("brag");
            if(enabled) {
                chatevents.enable();
            }
        } else if(f.equals(Feature.COINFLIP)) {
            enabled = config.getBoolean("coinflip.enabled");
            ce = coinflip;
            cmds.add("coinflip");
            if(enabled) {
                coinflip.enable();
            }
        } else if(f.equals(Feature.COLLECTION_FILTER)) {
            enabled = config.getBoolean("collection filter.enabled");
            ce = collectionfilter;
            cmds.add("collectionfilter");
            if(enabled) {
                collectionfilter.enable();
            }
        } else if(f.equals(Feature.CONQUEST)) {
            enabled = config.getBoolean("conquest.enabled");
            ce = conquest;
            cmds.add("conquest");
            if(enabled) {
                conquest.enable();
            }
        } else if(f.equals(Feature.CUSTOM_ARMOR)) {
            enabled = config.getBoolean("custom armor.enabled");
            if(enabled) {
                customarmor.enable();
            }
        } else if(f.equals(Feature.CUSTOM_BOSSES)) {
            enabled = config.getBoolean("custom bosses.enabled");
            if(enabled) {
                custombosses.enable();
            }
        } else if(f.equals(Feature.CUSTOM_ENCHANTS)) {
            enabled = config.getBoolean("custom enchants.enabled");
            ce = customenchants;
            cmds.add("alchemist");
            cmds.add("disabledenchants");
            cmds.add("enchanter");
            cmds.add("enchants");
            cmds.add("splitsouls");
            cmds.add("tinkerer");
            if(enabled) {
                customenchants.enable();
            }
        } else if(f.equals(Feature.CUSTOM_CREEPERS)) {
            enabled = config.getBoolean("custom creepers.enabled");
            if(enabled) {
                customexplosions.enable();
            }
        } else if(f.equals(Feature.CUSTOM_TNT)) {
            enabled = config.getBoolean("custom tnt.enabled");
            if(enabled) {
                customexplosions.enable();
            }
        } else if(f.equals(Feature.DUELS)) {
            enabled = config.getBoolean("duels.enabled");
            ce = duels;
            cmds.add("duel");
            ali.put("duel", "duels");
            if(enabled) {
                duels.enable();
            }
            /*
        } else if(f.equals(Feature.DUNGEONS)) {
            enabled = config.getBoolean("dungeon.enabled");
            ce = dungeons;
            cmds.add("dungeon");
            if(enabled) {
                dungeons.enable();
            }*/
        } else if(f.equals(Feature.ENVOY)) {
            enabled = config.getBoolean("envoy.enabled");
            ce = envoy;
            cmds.add("envoy");
            if(enabled) {
                envoy.enable();
            }
        } else if(f.equals(Feature.FACTION_UPGRADES)) {
            enabled = config.getBoolean("faction upgrades.enabled");
            if(enabled) {
                factionupgrades.enable();
            }
        } else if(f.equals(Feature.FUND)) {
            enabled = config.getBoolean("fund.enabled");
            ce = fund;
            cmds.add("fund");
            if(enabled) {
                fund.enable();
            }
        } else if(f.equals(Feature.GLOBAL_CHALLENGES)) {
            enabled = config.getBoolean("global challenges.enabled");
            ce = globalchallenges;
            cmds.add("challenge");
            ali.put("challenge", "global challenges");
            if(enabled) {
                globalchallenges.enable();
            }
        } else if(f.equals(Feature.HOMES)) {
            enabled = config.getBoolean("home.enabled");
            ce = homes;
            cmds.add("home");
            cmds.add("sethome");
            if(enabled) {
                homes.enable();
            }
        } else if(f.equals(Feature.ITEM_FILTER)) {
            enabled = config.getBoolean("item filter.enabled");
            ce = itemfilter;
            cmds.add("filter");
            ali.put("filter", "item filter");
            if(enabled) {
                itemfilter.enable();
            }
        } else if(f.equals(Feature.JACKPOT)) {
            enabled = config.getBoolean("jackpot.enabled");
            ce = jackpot;
            cmds.add("jackpot");
            if(enabled) {
                jackpot.enable();
            }
        } else if(f.equals(Feature.KITS_EVOLUTION)) {
            enabled = config.getBoolean("vkits.enabled");
            ce = kits;
            cmds.add("vkit");
            ali.put("vkit", "vkits");
            if(enabled) {
                for(String pc : cmds) {
                    getCommand(pc).setTabCompleter(kits);
                }
                kits.enableVkits();
            }
        } else if(f.equals(Feature.KITS_GLOBAL)) {
            enabled = config.getBoolean("gkits.enabled");
            ce = kits;
            cmds.add("gkit");
            ali.put("gkit", "gkits");
            if(enabled) {
                for(String pc : cmds) {
                    getCommand(pc).setTabCompleter(kits);
                }
                kits.enableGkits();
            }
        } else if(f.equals(Feature.KITS_MASTERY)) {
            enabled = config.getBoolean("mkits.enabled");
            ce = kits;
            cmds.add("mkit");
            ali.put("mkit", "mkits");
            if(enabled) {
                for(String pc : cmds) {
                    getCommand(pc).setTabCompleter(kits);
                }
                kits.enableMkits();
            }
        } else if(f.equals(Feature.KOTH)) {
            enabled = config.getBoolean("kingofthehill.enabled");
            ce = koth;
            cmds.add("kingofthehill");
            if(enabled) {
                koth.enable();
            }
        } else if(f.equals(Feature.LAST_MAN_STANDING)) {
            enabled = config.getBoolean("last man standing.enabled");
            ce = lastmanstanding;
            cmds.add("lastmanstanding");
            ali.put("lastmanstanding", "last man standing");
            if(enabled) {
                lastmanstanding.enable();
            }
        } else if(f.equals(Feature.LOOTBOXES)) {
            enabled = config.getBoolean("lootboxes.enabled");
            ce = lootboxes;
            cmds.add("lootbox");
            ali.put("lootbox", "lootboxes");
            if(enabled) {
                lootboxes.enable();
            }
        } else if(f.equals(Feature.MASKS)) {
            enabled = config.getBoolean("masks.enabled");
            if(enabled) {
                masks.enable();
            }
        } else if(f.equals(Feature.MOB_STACKER)) {
            enabled = config.getBoolean("mob stacker.enabled");
            if(enabled) {
                mobstacker.enable();
            }
        } else if(f.equals(Feature.MONTHLY_CRATES)) {
            enabled = config.getBoolean("monthly crates.enabled");
            ce = monthlycrates;
            cmds.add("monthlycrate");
            ali.put("monthlycrate", "monthly crates");
            if(enabled) {
                monthlycrates.enable();
            }
        } else if(f.equals(Feature.OUTPOSTS)) {
            enabled = config.getBoolean("outposts.enabled");
            ce = outposts;
            cmds.add("outpost");
            ali.put("outpost", "outposts");
            if(enabled) {
                outposts.enable();
            }
        } else if(f.equals(Feature.PETS)) {
            enabled = config.getBoolean("pets.enabled");
            if(enabled) {
                pets.enable();
            }
        } else if(f.equals(Feature.PLAYER_QUESTS)) {
            enabled = config.getBoolean("player quests.enabled");
            ce = playerquests;
            cmds.add("quest");
            ali.put("quest", "player quests");
            if(enabled) {
                playerquests.enable();
            }
        } else if(f.equals(Feature.SECONDARY_EVENTS)) {
            enabled = config.getBoolean("balance.enabled") || config.getBoolean("bless.enabled") || config.getBoolean("bump.enabled") || config.getBoolean("combine.enabled") || config.getBoolean("confirm.enabled") || config.getBoolean("roll.enabled") || config.getBoolean("withdraw.enabled") || config.getBoolean("xpbottle.enabled");
            ce = secondaryevents;
            cmds.add("balance");
            cmds.add("bless");
            cmds.add("bump");
            cmds.add("combine");
            cmds.add("confirm");
            cmds.add("roll");
            cmds.add("withdraw");
            cmds.add("xpbottle");
            if(enabled) {
                secondaryevents.enable();
            }
        } else if(f.equals(Feature.SERVER_CRATES)) {
            enabled = config.getBoolean("server crates.enabled");
            if(enabled) {
                servercrates.enable();
            }
        } else if(f.equals(Feature.SHOP)) {
            enabled = config.getBoolean("shop.enabled");
            ce = shop;
            cmds.add("shop");
            if(enabled) {
                shop.enable();
            }
        } else if(f.equals(Feature.SHOWCASE)) {
            enabled = config.getBoolean("showcase.enabled");
            ce = showcase;
            cmds.add("showcase");
            if(enabled) {
                showcase.enable();
            }
        } else if(f.equals(Feature.TITLES)) {
            enabled = config.getBoolean("title.enabled");
            ce = titles;
            cmds.add("title");
            if(enabled) {
                titles.enable();
            }
        } else if(f.equals(Feature.TRADE)) {
            enabled = config.getBoolean("trade.enabled");
            ce = trade;
            cmds.add("trade");
            if(enabled) {
                trade.enable();
            }
        } else if(f.equals(Feature.TRINKETS)) {
            enabled = config.getBoolean("trinkets.enabled");
            if(enabled) {
                trinkets.enable();
            }
        } else if(f.equals(Feature.WILD_PVP)) {
            enabled = config.getBoolean("wild pvp.enabled");
            ce = wildpvp;
            cmds.add("wildpvp");
            ali.put("wildpvp", "wild pvp");
            if(enabled) {
                wildpvp.enable();
            }
        }
        final boolean empty = cmds.isEmpty();
        if(enabled && !empty) {
            try {
                int v = 0;
                for(String cmd : cmds) {
                    PluginCommand pc = getCommand(cmd);
                    if(ce != null) {
                        if(pc == null) {
                            final String s = cmds.get(v);
                            knownCommands.put(s, originalCommands.get(s));
                            pc = (PluginCommand) knownCommands.get(s);
                        }
                        pc.setExecutor(ce);
                    }
                    final String k = pc.getName();
                    if(ali.isEmpty()) addAliases(k);
                    else addAliases(k, ali.get(k));
                    v += 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(!empty) {
            for(String cmd : cmds) {
                final PluginCommand pc = getCommand(cmd);
                if(pc != null) unregisterPluginCommand(pc);
            }
        }
    }

    private void addAliases(String cmd) throws Exception { addAliases(cmd, cmd); }
    private void addAliases(String cmd, String path) throws Exception {
        Object result = getPrivateField(getServer().getPluginManager(), "commandMap");
        SimpleCommandMap commandMap = (SimpleCommandMap) result;
        Object map = getPrivateField(commandMap, "knownCommands");
        @SuppressWarnings("unchecked")
        HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
        for(String alias : config.getStringList(path + ".aliases")) {
            if(!alias.equals(cmd)) {
                final String c = cmd;
                knownCommands.put(alias, getCommand(c));
                final PluginCommand pc = Bukkit.getPluginCommand(c);
                if(pc != null) pc.getAliases().add(alias);
            }
        }
    }
    private Object getPrivateField(Object object, String field) throws Exception {
        /* Code from "zeeveener" at https://bukkit.org/threads/how-to-unregister-commands-from-your-plugin.131808/ , edited by RandomHashTags */
        Class<?> clazz = object.getClass();
        Field objectField = field.equals("commandMap") ? clazz.getDeclaredField(field) : field.equals("knownCommands") ? v.contains("1.8") || v.contains("1.9") || v.contains("1.10") || v.contains("1.11") || v.contains("1.12") || v.equals("1.13") ? clazz.getDeclaredField(field) : clazz.getSuperclass().getDeclaredField(field) : null;
        if(objectField == null) {
            Bukkit.broadcastMessage("objectField == null!");
            return null;
        }
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }
    private void unregisterPluginCommand(PluginCommand cmd) {
        final String c = cmd.getName();
        knownCommands.remove("randompackage:" + c);
        cmd.unregister(commandMap);
        Y.remove("RandomPackage:" + c);
        boolean hasOtherCmd = false;
        for(int i = 0; i < Y.keySet().size(); i++) {
            final String otherCmd = (String) Y.keySet().toArray()[i];
            if(!otherCmd.startsWith("RandomPackage:") && otherCmd.split(":")[otherCmd.split(":").length-1].equals(c)) { // gives the last plugin that has the cmd.getName() the command priority
                hasOtherCmd = true;
                knownCommands.replace(c, cmd, (PluginCommand) Y.values().toArray()[i]);
            }
        }
        if(!hasOtherCmd) // removes the command completely
            knownCommands.remove(c);
    }

    /*
     *
     * PlayerArmorEvent Listener
     *
     */
    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(!event.isCancelled() && !event.getClick().equals(ClickType.DOUBLE_CLICK) && event.getCurrentItem() != null && event.getCursor() != null && event.getInventory().getType().equals(InventoryType.CRAFTING)) {
            final Player player = (Player) event.getWhoClicked();
            final InventoryType.SlotType st = event.getSlotType();
            final ClickType ct = event.getClick();
            final ItemStack cursoritem = event.getCursor(), currentitem = event.getCurrentItem();
            final PlayerInventory inv = player.getInventory();
            final String cursor = cursoritem.getType().name(), current = currentitem.getType().name();
            if((st.equals(InventoryType.SlotType.QUICKBAR) || st.equals(InventoryType.SlotType.CONTAINER)) && ct.equals(ClickType.CONTROL_DROP)) return;
            PlayerArmorEvent a = null, b = null;
            if(st.equals(InventoryType.SlotType.ARMOR) && ct.equals(ClickType.NUMBER_KEY)) {
                final int rawslot = event.getRawSlot();
                final ItemStack prev = inv.getItem(event.getSlot()), hb = inv.getItem(event.getHotbarButton());
                final String t = hb != null ? hb.getType().name() : "AIR";
                if(prev != null && !prev.getType().name().equals("AIR"))
                    a = new PlayerArmorEvent(player, PlayerArmorEvent.ArmorEventReason.NUMBER_KEY_UNEQUIP, prev);
                if(canBeUsed(rawslot, t))
                    b = new PlayerArmorEvent(player, PlayerArmorEvent.ArmorEventReason.NUMBER_KEY_EQUIP, hb);
            } else if(event.isShiftClick()) {
                if(st.equals(InventoryType.SlotType.ARMOR))
                    a = new PlayerArmorEvent(player, PlayerArmorEvent.ArmorEventReason.SHIFT_UNEQUIP, currentitem);
                else {
                    final int t = getTargetSlot(current);
                    if(t == -1) return;
                    final ItemStack prevArmor = inv.getArmorContents()[t == 5 ? 3 : t == 6 ? 2 : t == 7 ? 1 : 0];
                    if((prevArmor == null || prevArmor.getType().equals(Material.AIR)) && canBeUsed(t, current)) a = new PlayerArmorEvent(player, PlayerArmorEvent.ArmorEventReason.SHIFT_EQUIP, currentitem);
                }
            } else if(st.equals(InventoryType.SlotType.ARMOR)) {
                if(ct.name().contains("DROP") && !current.equals("AIR")) {
                    a = new PlayerArmorEvent(player, PlayerArmorEvent.ArmorEventReason.DROP, currentitem);
                } else if(ct.equals(ClickType.LEFT) || ct.equals(ClickType.RIGHT)) {
                    final int rawslot = event.getRawSlot();
                    if(!current.equals("AIR")) {
                        final int c1 = getTargetSlot(current), c2 = getTargetSlot(cursor);
                        if(c1 == c2 || rawslot == c1)
                            a = new PlayerArmorEvent(player, PlayerArmorEvent.ArmorEventReason.INVENTORY_UNEQUIP, currentitem);
                    }
                    if(!cursor.equals("AIR")) {
                        final int c1 = getTargetSlot(current), c2 = getTargetSlot(cursor);
                        if(c1 == c2 || rawslot == c2)
                            b = new PlayerArmorEvent(player, PlayerArmorEvent.ArmorEventReason.INVENTORY_EQUIP, cursoritem);
                    }
                }
            }
            if(a != null) {
                pm.callEvent(a);
                final ItemStack y = a.getCurrentItem(), z = a.getCursor();
                if(y != null) event.setCurrentItem(y);
                if(z != null) event.setCursor(z);
            }
            if(b != null) {
                pm.callEvent(b);
                final ItemStack y = b.getCurrentItem(), z = b.getCursor();
                if(y != null) event.setCurrentItem(y);
                if(z != null) event.setCursor(z);
            }
        }
    }
    private int getTargetSlot(String target) {
        return target.contains("HELMET") || target.contains("SKULL") || target.contains("HEAD") ? 5
                : target.contains("CHESTPLATE") || target.contains("ELYTRA") ? 6
                : target.contains("LEGGINGS") ? 7
                : target.contains("BOOTS") ? 8
                : -1;
    }
    private boolean canBeUsed(int rawslot, String target) {
        return rawslot == 5 && (target.contains("HELMET") || target.contains("SKULL") || target.contains("HEAD"))
                || rawslot == 6 && (target.contains("CHESTPLATE") || target.contains("ELYTRA"))
                || rawslot == 7 && target.contains("LEGGINGS")
                || rawslot == 8 && target.contains("BOOTS");
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        if(event.getItem() != null && event.getAction().name().contains("RIGHT")) {
            final ItemStack i = event.getItem().clone();
            final String item = i.getType().name();
            final Player player = event.getPlayer();
            final PlayerInventory PI = player.getInventory();
            if(item.endsWith("HELMET") && PI.getHelmet() == null || item.endsWith("CHESTPLATE") && PI.getChestplate() == null || item.endsWith("LEGGINGS") && PI.getLeggings() == null || item.endsWith("BOOTS") && PI.getBoots() == null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                    if(!player.getGameMode().equals(GameMode.CREATIVE) && player.getItemInHand().equals(i)) return;
                    PlayerArmorEvent armorevent = new PlayerArmorEvent(player, PlayerArmorEvent.ArmorEventReason.HOTBAR_EQUIP, i);
                    pm.callEvent(armorevent);
                }, 0);
            } else if(item.endsWith("HELMET") && PI.getHelmet() != null || item.endsWith("CHESTPLATE") && PI.getChestplate() != null || item.endsWith("LEGGINGS") && PI.getLeggings() != null || item.endsWith("BOOTS") && PI.getBoots() != null) {
                PlayerArmorEvent armorevent = new PlayerArmorEvent(player, PlayerArmorEvent.ArmorEventReason.HOTBAR_SWAP, i);
                pm.callEvent(armorevent);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerItemBreakEvent(PlayerItemBreakEvent event) {
        final ItemStack i = event.getBrokenItem();
        if(i.getType().name().endsWith("HELMET") || i.getType().name().endsWith("CHESTPLATE") || i.getType().name().endsWith("LEGGINGS") || i.getType().name().endsWith("BOOTS")) {
            PlayerArmorEvent armorevent = new PlayerArmorEvent(event.getPlayer(), PlayerArmorEvent.ArmorEventReason.BREAK, i);
            pm.callEvent(armorevent);
        }
    }
}
