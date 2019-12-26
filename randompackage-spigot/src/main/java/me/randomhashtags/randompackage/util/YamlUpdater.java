package me.randomhashtags.randompackage.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public abstract class YamlUpdater {
    protected boolean updateYaml(String folder, File file) {
        if(file.exists()) {
            final LinkedHashMap<String, Object> changes = getChanges(folder, file);
            if(changes != null && !changes.isEmpty()) {
                final YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                boolean changed = false;
                final ConfigurationSection section = yml.getConfigurationSection("");
                final Set<String> keys = section.getKeys(true);
                for(String change : changes.keySet()) {
                    if(!keys.contains(change)) {
                        changed = true;
                        yml.set(change, changes.get(change));
                    }
                }
                if(changed) {
                    System.out.println("[RandomPackage] Updated file \"" + file.getName() + "\" with new contents!");
                    try {
                        yml.save(file);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                return changed;
            }
        }
        return false;
    }

    public LinkedHashMap<String, Object> getChanges(String folder, File file) { // Implemented since v16.3.1 - Updated v16.5.0
        final String n = file.getName().split("\\.yml")[0];
        switch (n) {
            case "_settings":
                switch (folder) {
                    case "conquests": return getConquests();
                    case "custom armor": return getCustomArmor();
                    case "custom enchants": return getCustomEnchants();
                    case "duel arenas": return getDuels();
                    case "dungeons": return getDungeons();
                    case "envoy tiers": return getEnvoy();
                    case "faction quests": return getFactionQuests();
                    case "faction upgrades": return getFactionUpgrades();
                    case "filter categories": return getItemFilter();
                    case "global challenges": return getGlobalChallenges();
                    case "kits": return getKits();
                    case "masks": return getMasks();
                    case "monthly crates": return getMonthlyCrates();
                    case "outposts": return getOutposts();
                    case "player quests": return getPlayerQuests();
                    case "shops": return getShop();
                    case "strongholds": return getStrongholds();
                    case "trinkets": return getTrinkets();
                    default: return null;
                }
            case "auction house": return getAH();
            case "battle royale": return getBattleRoyale();
            case "coinflip": return getCoinFlip();
            case "collection filter": return getCollectionFilter();
            case "config": return getConfig();
            case "custom explosions": return getCustomExplosions();
            case "disguises": return getDisguises();
            case "faction points": return getFactionPoints();
            case "fund": return getFund();
            case "homes": return getHomes();
            case "items": return getItems();
            case "jackpot": return getJackpot();
            case "koth": return getKOTH();
            case "last man standing": return getLastManStanding();
            case "lootboxes": return getLootboxes();
            case "mob stacker": return getMobStacker();
            case "reputation": return getReputation();
            case "secondary": return getSecondary();
            case "showcase": return getShowcase();
            case "spawner stacking": return getSpawnerStacking();
            case "titles": return getTitles();
            case "trade": return getTrade();
            case "wild": return getWild();
            case "wild pvp": return getWildPvP();
            // Addons
            case "soul trackers": return getSoulTrackers();
            default: return null;
        }
    }

    private LinkedHashMap<String, Object> putAll(Object[] values) {
        final List<Object> objects = new ArrayList<>();
        objects.addAll(Arrays.asList(values));
        final LinkedHashMap<String, Object> tree = new LinkedHashMap<>();
        for(Object o : values) {
            if(o instanceof Object[]) {
                final Object[] obj = (Object[]) o;
                for(int z = 0; z < obj.length; z++) {
                    if(z%2 == 1) {
                        tree.put((String) obj[z-1], obj[z]);
                    }
                }
                objects.remove(o);
            }
        }
        int i = 0;
        for(Object obj : objects) {
            if(i%2 == 1) {
                tree.put(objects.get(i-1).toString(), obj);
            }
            i++;
        }
        return tree;
    }

    private LinkedHashMap<String, Object> getConfig() {
        final Object[] values = new Object[] {
                // 16.3.3
                "backup interval", 360,
                // 16.4.0
                "boosters.enabled", true,
                "custom enchants.transmog scrolls", true,
                "custom enchants.white scrolls", true,
                "wild.enabled", true, // enabled in 16.4.3
                // 16.4.3
                "file system.enabled", false,
                "file system.type", "LOCAL/MYSQL/MANGODB",
                "file system.host", "localhost",
                "file system.port", 3306,
                "file system.database", "GlobalBase",
                "file system.username", "username",
                "file system.password", "password",
                "supported plugins.standalone.PlaceholderAPI", true,
                "supported plugins.mechanics.SilkSpawners", true,
                "supported plugins.mechanics.EpicSpawners", true,
                "supported plugins.mechanics.MCMMO", true,
                "supported plugins.regional.FactionsUUID", true,
                "supported plugins.regional.ASkyblock", true,
                "supported plugins.regional.SuperiorSkyblock", true,
                "supported plugins.regional.EpicSkyblock", true,

                "alchemist.cmds", Arrays.asList("alchemist"),
                "auction house.cmds", Arrays.asList("auctionhouse", "ah", "auction"),
                "balance.cmds", Arrays.asList("balance", "bal"),
                "bless.cmds", Arrays.asList("bless"),
                "bump.cmds", Arrays.asList("bump"),
                "coinflip.cmds", Arrays.asList("coinflip", "cf"),
                "combine.cmds", Arrays.asList("combine"),
                "confirm.cmds", Arrays.asList("confirm"),
                "collection filter.cmds", Arrays.asList("collectionfilter"),
                "conquest.cmds", Arrays.asList("conquest"),
                "disabled enchants.enabled", true,
                "disabled enchants.cmds", Arrays.asList("disabledenchants"),
                "duels.cmds", Arrays.asList("duel", "duels"),
                "dungeons.cmds", Arrays.asList("dungeon", "dungeons"),
                "enchants.cmds", Arrays.asList("enchants"),
                "enchanter.cmds", Arrays.asList("enchanter"),
                "envoy.cmds", Arrays.asList("envoy"),
                "faction upgrades.cmds", Arrays.asList("f upgrade"),
                "fund.cmds", Arrays.asList("fund"),
                "gkits.cmds", Arrays.asList("gkit", "gkits"),
                "global challenges.cmds", Arrays.asList("globalchallenge", "globalchallenges", "challenges"),
                "home.cmds", Arrays.asList("home", "homes"),
                "item filter.cmds", Arrays.asList("filter"),
                "jackpot.cmds", Arrays.asList("jackpot"),
                "kingofthehill.cmds", Arrays.asList("kingofthehill", "koth"),
                "last man standing.cmds", Arrays.asList("lastmanstanding", "lms", "warzone"),
                "lootboxes.cmds", Arrays.asList("lootbox", "lootboxes"),
                "mkits.cmds", Arrays.asList("mkit", "mkits"),
                "monthly crates.cmds", Arrays.asList("monthlycrate", "monthlycrates", "cc", "mc"),
                "outposts.cmds", Arrays.asList("outpost", "outposts"),
                "player quests.cmds", Arrays.asList("quest", "quests"),
                "wild pvp.cmds", Arrays.asList("wildpvp", "pvp"),
                "raid events.enabled", false,
                "raid events.cmds", Arrays.asList("raid"),
                "roll.cmds", Arrays.asList("roll"),
                "shop.cmds", Arrays.asList("shop"),
                "showcase.cmds", Arrays.asList("showcase"),
                "spawner stacking.enabled", false,
                "splitsouls.enabled", true,
                "splitsouls.cmds", Arrays.asList("splitsouls"),
                "tinkerer.cmds", Arrays.asList("tinkerer"),
                "title.cmds", Arrays.asList("title", "titles"),
                "trade.cmds", Arrays.asList("trade"),
                "vkits.cmds", Arrays.asList("vkit", "vkits"),
                "wild.cmds", Arrays.asList("wild", "wilderness"),
                "withdraw.cmds", Arrays.asList("withdraw"),
                "xpbottle.cmds", Arrays.asList("xpbottle"),
                // 16.5.0
                "fat buckets.enabled", true,
                "inventory pets.enabled", false,
                "sethome.enabled", true,
                "sethome.cmds", Arrays.asList("sethome"),
                // 16.5.8
                "supported plugins.regional.WorldGuard", true,
                // 16.5.9
                "slot bot.enabled", true,
                "slot bot.cmds", Arrays.asList("slotbot", "slotbots", "bot", "bots", "slot")
        };
        return putAll(values);
    }

    private LinkedHashMap<String, Object> getAH() {
        return null;
    }
    private LinkedHashMap<String, Object> getBattleRoyale() {
        return null;
    }
    private LinkedHashMap<String, Object> getCoinFlip() {
        final Object[] values = new Object[] {
                // 16.5.0
                "messages.toggle notifications.on", newStringList("&7Coin Flip notifications turned &a&lON."),
                "messages.toggle notifications.off", newStringList("&7Coin Flip notifications turned &c&lOFF."),
                "messages.help", newStringList("&e&l&nCoin Flip Help", "&e/coinflip", "&7View all active Coin Flip matches.", "&e/coinflip <amount>", "&7Start a Coin Flip match with the given wager.", "&e/coinflip cancel", "&7Cancel the pending Coin Flip match.", "&e/coinflip toggle", "&7Toggle Coin Flip win notifications.", "&e/coinflip stats", "&7View your current Coin Flip stats.")
        };
        return putAll(values);
    }
    private LinkedHashMap<String, Object> getCollectionFilter() {
        final Object[] values = new Object[] {
                // 16.3.3
                newInventory("types.iron", "Select Iron Type", 9),
                newItemStack("types.iron.all iron", 0, "heavy_weighted_pressure_plate", "All Iron", newStringList("&7Click to pickup Iron Ingots and Blocks!"), "iron_ingot;iron_block"),
                newItemStack("types.iron.iron ingot", 1, "iron_ingot", "Iron Ingot", newStringList("&7Click to only pickup Iron Ingots!"), "ingot_ingot"),
                newItemStack("types.iron.iron block", 2, "iron_block", "Iron Block", newStringList("&7Click to only pickup Iron Blocks!"), "iron_block"),
                newInventory("types.gold", "Select Gold Type", 9),
                newItemStack("types.gold.all gold", 0, "light_weighted_pressure_plate", "All Gold", newStringList("&7Click to pickup Gold Ingots, Nuggets and Blocks!"), "gold_ingot;gold_nugget;gold_block"),
                newItemStack("types.gold.gold ingot", 1, "gold_ingot", "Gold Ingot", newStringList("&7Click to only pickup Gold Ingots!"), "gold_ingot"),
                newItemStack("types.gold.gold nugget", 1, "gold_nugget", "Gold Nugget", newStringList("&7Click to only pickup Gold Nuggets!"), "gold_nugget"),
                newItemStack("types.gold.gold block", 1, "gold_block", "Gold Block", newStringList("&7Click to only pickup Gold Blocks!"), "gold_block"),
        };
        return putAll(values);
    }
    private LinkedHashMap<String, Object> getConquests() {
        return null;
    }
    private LinkedHashMap<String, Object> getCustomArmor() {
        final Object[] values = new Object[] {
                // 16.5.0
                newItemStack("items.crystal", "nether_star", "&6&lArmor Crystal (&r{NAME}&r&6&l)", newStringList("&a{PERCENT}% Success Rate", "&7Can be applied to any non", "&7armor set that is not", "&7already equipped with a", "&7bonus crystal to gain", "&7a passive advantage!", " ", "&6&lCrystal Bonus:", " {NAME}", "{PERKS}")),
                "items.crystal.applied lore", "&6&lArmor Crystal (&r{NAME}}&r&6&l)",
                // 16.5.3
                "messages.cannot apply.armor set piece", newStringList("&c&l(!)&r &cYou cannot apply an Armor Set Crystal to an existing Armor Set piece!"),
                "messages.cannot apply.already has crystal", newStringList("&c&l(!)&r &cYou cannot apply multiple Armor Set crystals to a single piece of gear!")
        };
        return putAll(values);
    }
    private LinkedHashMap<String, Object> getCustomEnchants() {
        final Object[] values = new Object[] {
                "settings.enabled worlds", newStringList("world", "world_nether", "world_the_end")
        };
        return putAll(values);
    }
    private LinkedHashMap<String, Object> getCustomExplosions() {
        return null;
    }
    private LinkedHashMap<String, Object> getDisguises() {
        return null;
    }
    private LinkedHashMap<String, Object> getDuels() {
        return null;
    }
    private LinkedHashMap<String, Object> getDungeons() {
        return null;
    }
    private LinkedHashMap<String, Object> getEnvoy() {
        return null;
    }
    private LinkedHashMap<String, Object> getFactionPoints() {
        return null;
    }
    private LinkedHashMap<String, Object> getFactionQuests() {
        return null;
    }
    private LinkedHashMap<String, Object> getFactionUpgrades() {
        return null;
    }
    private LinkedHashMap<String, Object> getFund() {
        return null;
    }
    private LinkedHashMap<String, Object> getGlobalChallenges() {
        return null;
    }
    private LinkedHashMap<String, Object> getHomes() {
        final Object[] values = new Object[] {
                // 16.4.3
                "messages.deleted due to inside a faction claim", newStringList("&c&l(!)&r &cHome &l{HOME}&r &7({X}x, {Y}y, {Z}z) &chas been removed due to it being inside a ally/truce/neutral/enemy faction claim."),
                "settings.regional.factions.remove inside claims upon leaving", true
        };
        return putAll(values);
    }
    private LinkedHashMap<String, Object> getItemFilter() {
        return null;
    }
    private LinkedHashMap<String, Object> getItems() {
        final Object[] values = new Object[] {
                // 16.4.1
                newItemStack("christmas candy", "glowstone_dust", "&eChristmas Candy &7(Right Click)", newStringList("&7A yummy piece of space candy, only", "&7obtainable during &nChristmas 2016")),
                // 16.4.3
                newItemStack("custom items.Cupids Broken Heart", "fermented_spider_eye", "&4Cupid's &c&lBroken Heart", newStringList("&7The God of Loves aching heart!", "&7Will he ever find love?"))
        };
        return putAll(values);
    }
    private LinkedHashMap<String, Object> getJackpot() {
        final Object[] values = new Object[] {
                // 16.5.0
                "messages.toggle notifications.on", newStringList("&eJackpot Notifications &a&lENABLED"),
                "messages.toggle notifications.off", newStringList("&eJackpot Notifications &c&lDISABLED", "&7You will no longer see Jackpot countdown notifications!")
        };
        return putAll(values);
    }
    private LinkedHashMap<String, Object> getKits() {
        return null;
    }
    private LinkedHashMap<String, Object> getKOTH() {
        return null;
    }
    private LinkedHashMap<String, Object> getLastManStanding() {
        return null;
    }
    private LinkedHashMap<String, Object> getLootboxes() {
        final Object[] values = new Object[] {
                // 16.5.0
                newItemStack("items.mystery lootbox", "chest", "&6&lMystery Lootbox&r &7(Right Click)", newStringList(
                        "&7Click to receive one of the",
                        "&7following random lootboxes:",
                        " &6&l*&r &f&lLootbox:&r &2&l#&a&lLucky",
                        " &6&l*&r &f&lLootbox:&r &5&lDark Dreams",
                        " &6&l*&r &f&lLootbox:&r &4&lBox&r &c&lof&r &4&lChocolates",
                        " &6&l*&r &f&lLootbox:&r &4&l&a&lApril&r &2&lShowers",
                        " &6&l*&r &f&lLootbox:&r &9&lS&b&ln&9&lo&b&lw&r &f&lDay!",
                        " &6&l*&r &f&lLootbox:&r &e&lHangover",
                        " &6&l*&r &f&lLootbox:&r &d&lEaster&r &e&l2&b&l0&d&l1&a&l9",
                        " &6&l*&r &f&lLootbox:&r &9&lIcy Adventures",
                        " &6&l*&r &f&lLootbox:&r &5&lStackable",
                        " &6&l*&r &f&lLootbox:&r &e&lSummer&r &4&l&oSavage",
                        " &6&l*&r &f&lLootbox:&r &4&lDungeon&r &c&lCrawler",
                        " &6&l*&r &f&lLootbox:&r &c&lPrimal",
                        " &6&l*&r &f&lLootbox:&r &c&lCollectors&r &4&lEdition",
                        " &6&l*&r &f&lLootbox:&r &9&lParadox",
                        " &6&l*&r &f&lLootbox:&r &9&lSugar&r &5&lDaddy",
                        " &6&l*&r &f&lLootbox:&r &c&lR&6&la&e&li&a&ln&b&lb&d&lo&5&lw",
                        " &6&l*&r &f&lLootbox:&r &5&lSuper&d&lnova",
                        " &6&l*&r &f&lLootbox:&r &b&lServer&r &d&lCrate",
                        " &6&l*&r &f&lLootbox:&r &c&lE&4&lx&c&lp&4&ll&c&lo&4&ls&c&li&4&lv&c&le",
                        " &6&l*&r &f&lLootbox:&r &9&lPet&r &5&lCollector",
                        " &6&l*&r &f&lLootbox:&r &e&lEnd&r &6&lof&r &c&lSummer"
                        )
                ),
                "items.mystery lootbox.reward size", 1,
                "items.mystery lootbox.obtainable lootboxes", newStringList("LUCKY", "DARK_DREAMS", "BOX_OF_CHOCOLATES", "APRIL_SHOWERS", "SURVIVAL_KIT", "SNOW_DAY", "HANGOVER", "EASTER_2019", "ICY_ADVENTURES", "STACKABLE", "SUMMER_SAVAGE", "DUNGEON_CRAWLER", "PRIMAL", "COLLECTORS_EDITION", "PARADOX", "SUGAR_DADDY", "RAINBOW", "SUPERNOVA", "SERVER_CRATE", "PET_COLLECTOR", "END_OF_SUMMER")
        };
        return putAll(values);
    }
    private LinkedHashMap<String, Object> getMasks() {
        return null;
    }
    private LinkedHashMap<String, Object> getMobStacker() {
        return null;
    }
    private LinkedHashMap<String, Object> getMonthlyCrates() {
        return null;
    }
    private LinkedHashMap<String, Object> getOutposts() {
        return null;
    }
    private LinkedHashMap<String, Object> getPlayerQuests() {
        return null;
    }
    private LinkedHashMap<String, Object> getReputation() {
        return null;
    }
    private LinkedHashMap<String, Object> getSecondary() {
        return null;
    }
    private LinkedHashMap<String, Object> getShop() {
        return null;
    }
    private LinkedHashMap<String, Object> getShowcase() {
        return null;
    }
    private LinkedHashMap<String, Object> getSpawnerStacking() {
        return null;
    }
    private LinkedHashMap<String, Object> getStrongholds() {
        return null;
    }
    private LinkedHashMap<String, Object> getTitles() {
        return null;
    }
    private LinkedHashMap<String, Object> getTrade() {
        return null;
    }
    private LinkedHashMap<String, Object> getTrinkets() {
        return null;
    }
    private LinkedHashMap<String, Object> getWild() {
        return null;
    }
    private LinkedHashMap<String, Object> getWildPvP() {
        return null;
    }
    // Addons
    private LinkedHashMap<String, Object> getSoulTrackers() {
        final Object[] values = new Object[] {
                // 16.4.3
                "messages.need item with soul tracker", newStringList("&c&l(!)&r &cYou need an item with a soul tracker to use this!"),
                "messages.need to collect souls", newStringList("&c&l(!)&r &cYou need to collect some souls to use this!"),
                "messages.need to collect more souls", newStringList("&c&l(!)&r &cYou need more souls collected to use this!")
        };
        return putAll(values);
    }

    private Object[] newInventory(String key, String title, int size) {
        return new Object[] {
                key + ".title", title,
                key + ".size", size
        };
    }
    private Object[] newItemStack(String key, String material, String name, List<String> lore) {
        return new Object[] {
                key + ".item", material,
                key + ".name", name,
                key + ".lore", lore,
        };
    }
    private Object[] newItemStack(String key, int slot, String material, String name, List<String> lore, String picksup) {
        return new Object[] {
                key + ".slot", slot,
                key + ".item", material,
                key + ".name", name,
                key + ".lore", lore,
                key + ".picks up", picksup
        };
    }
    private List<String> newStringList(String...list) {
        return Arrays.asList(list);
    }
}