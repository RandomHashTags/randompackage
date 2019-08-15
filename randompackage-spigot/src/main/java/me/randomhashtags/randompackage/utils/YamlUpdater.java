package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.utils.RPFeature.rpd;

public class YamlUpdater extends UVersion {
    private static YamlUpdater instance;
    public static YamlUpdater getYamlUpdater() {
        if(instance == null) instance = new YamlUpdater();
        return instance;
    }

    private boolean update(File file) {
        if(file.exists()) {
            final LinkedHashMap<String, Object> changes = getChanges(file);
            if(changes != null && !changes.isEmpty()) {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
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
    public void update() {
        final String[] f = new String[] {
                "auction house",
                "coinflip",
                "collection filter",
                "config",
                "conquests",
                "custom armor",
                "custom enchants",
                "custom explosions",
                "disguises",
                "duels",
                "dungeons",
                "envoy",
                "faction points",
                "faction quests",
                "faction upgrades",
                "fund",
                "global challenges",
                "homes",
                "item filter",
                "items",
                "jackpot",
                "kits",
                "koth",
                "last man standing",
                "lootboxes",
                "masks",
                "mob stacker",
                "monthly crates",
                "outposts",
                "player quests",
                "secondary",
                "shop",
                "showcase",
                "titles",
                "trade",
                "wild pvp",
        };
        final File d = rpd;
        final List<String> updatedymls = new ArrayList<>();
        for(String s : f) {
            final File y = new File(d, s + ".yml");
            if(y.exists()) {
                final boolean updated = update(y);
                if(updated) updatedymls.add(s + ".yml");
            }
        }
        sendConsoleMessage("&6[RandomPackage] &a" + (!updatedymls.isEmpty() ? "Updated the following ymls: &e" + updatedymls.toString() : "All files up to date"));
    }

    public LinkedHashMap<String, Object> getChanges(File file) { // Implemented since v16.3.1
        final String n = file.getName().split("\\.yml")[0];
        switch(n) {
            case "auction house": return getAH();
            case "coinflip": return getCoinFlip();
            case "collection filter": return getCollectionFilter();
            case "config": return getConfig();
            case "conquests": return getConquests();
            case "custom armor": return getCustomArmor();
            case "custom enchants": return getCustomEnchants();
            case "custom explosions": return getCustomExplosions();
            case "disguises": return getDisguises();
            case "duels": return getDuels();
            case "dungeons": return getDungeons();
            case "envoy": return getEnvoy();
            case "faction points": return getFactionPoints();
            case "faction quests": return getFactionQuests();
            case "faction upgrades": return getFactionUpgrades();
            case "fund": return getFund();
            case "global challenges": return getGlobalChallenges();
            case "homes": return getHomes();
            case "item filter": return getItemFilter();
            case "items": return getItems();
            case "jackpot": return getJackpot();
            case "kits": return getKits();
            case "koth": return getKOTH();
            case "last man standing": return getLastManStanding();
            case "lootboxes": return getLootboxes();
            case "masks": return getMasks();
            case "mob stacker": return getMobStacker();
            case "monthly crates": return getMonthlyCrates();
            case "outposts": return getOutposts();
            case "player quests": return getPlayerQuests();
            case "secondary": return getSecondary();
            case "shop": return getShop();
            case "showcase": return getShowcase();
            case "titles": return getTitles();
            case "trade": return getTrade();
            case "wild pvp": return getWildPvP();
            default: return null;
        }
    }

    private LinkedHashMap<String, Object> putAll(Object[] values) {
        final LinkedHashMap<String, Object> tree = new LinkedHashMap<>();
        for(int i = 0; i < values.length; i++) {
            final Object o = values[i];
            if(o instanceof Object[]) {
                final Object[] obj = (Object[]) o;
                for(int z = 0; z < obj.length; z++) {
                    if(z%2 == 1) {
                        tree.put((String) obj[z-1], obj[z]);
                    }
                }
            } else if(i%2 == 1) {
                tree.put(values[i-1].toString(), o);
            }
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
                "wild.enabled", false,
                // 16.4.3
                "file system.enabled", false,
                "file system.type", "LOCAL/MYSQL/MANGODB",
                "file system.host", "localhost",
                "file system.port", 3306,
                "file system.database", "GlobalBase",
                "file system.username", "username",
                "file system.password", "password",
                "supported plugins.standalone.PlaceholderAPI", true,
                "supported plugins.SilkSpawners", true,
                "supported plugins.EpicSpawners", true,
                "supported plugins.mechanics.MCMMO Classic", true,
                "supported plugins.mechanics.MCMMO Overhaul", false,
                "supported plugins.regional.FactionsUUID", true,
                "supported plugins.regional.ASkyblock", true,
                "supported plugins.regional.SuperiorSkyblock", true,

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
                "spawner stacking.enabled", true,
                "splitsouls.enabled", true,
                "splitsouls.cmds", Arrays.asList("splutsouls"),
                "tinkerer.cmds", Arrays.asList("tinkerer"),
                "title.cmds", Arrays.asList("title", "titles"),
                "trade.cmds", Arrays.asList("trade"),
                "vkits.cmds", Arrays.asList("vkit", "vkits"),
                "wild.cmds", Arrays.asList("wild"),
                "withdraw.cmds", Arrays.asList("withdraw"),
                "xpbottle.cmds", Arrays.asList("cmds"),
        };
        return putAll(values);
    }


    private LinkedHashMap<String, Object> getAH() {
        return null;
    }
    private LinkedHashMap<String, Object> getCoinFlip() {
        return null;
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
        return null;
    }
    private LinkedHashMap<String, Object> getCustomEnchants() {
        return null;
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
        return null;
    }
    private LinkedHashMap<String, Object> getItemFilter() {
        return null;
    }
    private LinkedHashMap<String, Object> getItems() {
        final Object[] values = new Object[] {
                // 16.4.1
                newItemStack("christmas candy", "glowstone_dust", "&eChristmas Candy &7(Right Click)", newStringList("&7A yummy piece of space candy, only", "&7obtainable during &nChristmas 2016"))
        };
        return putAll(values);
    }
    private LinkedHashMap<String, Object> getJackpot() {
        return null;
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
        return null;
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
    private LinkedHashMap<String, Object> getSecondary() {
        return null;
    }
    private LinkedHashMap<String, Object> getShop() {
        return null;
    }
    private LinkedHashMap<String, Object> getShowcase() {
        return null;
    }
    private LinkedHashMap<String, Object> getTitles() {
        return null;
    }
    private LinkedHashMap<String, Object> getTrade() {
        return null;
    }
    private LinkedHashMap<String, Object> getWildPvP() {
        return null;
    }


    private Object[] newInventory(String key, String title, int size) {
        return new Object[]{
                key + ".title", title,
                key + ".size", size
        };
    }
    private Object[] newItemStack(String key, String material, String name, List<String> lore) {
        return new Object[]{
                key + ".item", material,
                key + ".name", name,
                key + ".lore", lore,
        };
    }
    private Object[] newItemStack(String key, int slot, String material, String name, List<String> lore, String picksup) {
        return new Object[]{
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