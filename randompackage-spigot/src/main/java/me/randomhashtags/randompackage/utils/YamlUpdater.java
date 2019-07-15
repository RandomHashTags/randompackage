package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class YamlUpdater extends UVersion {
    private static YamlUpdater instance;
    public static YamlUpdater getYmlUpdater() {
        if(instance == null) instance = new YamlUpdater();
        return instance;
    }

    public void update(File file) {
        if(file.exists()) {
            final TreeMap<String, Object> changes = getChanges(file);
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
                    System.out.println("Updating file \"" + file.getName() + "\" with new contents!");
                    try {
                        yml.save(file);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public TreeMap<String, Object> getChanges(File file) { // since v16.3.1
        final String n = file.getName().split("\\.yml")[0];
        switch(n) {
            case "auction house": return getAH();
            case "coinflip": return getCoinFlip();
            case "collection filter": return getCollectionFilter();
            case "config": return getConfig();
            case "conquests": return getConquests();
            case "custom armor": return getCustomArmor();
            case "custom enchants": return getCustomEnchants();
            case "custom explosions": return getCustomExplosiosn();
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

    private TreeMap<String, Object> getConfig() {
        return null;
    }


    private TreeMap<String, Object> getAH() {
        return null;
    }
    private TreeMap<String, Object> getCoinFlip() {
        return null;
    }
    private TreeMap<String, Object> getCollectionFilter() {
        return null;
    }
    private TreeMap<String, Object> getConquests() {
        return null;
    }
    private TreeMap<String, Object> getCustomArmor() {
        return null;
    }
    private TreeMap<String, Object> getCustomEnchants() {
        return null;
    }
    private TreeMap<String, Object> getCustomExplosiosn() {
        return null;
    }
    private TreeMap<String, Object> getDisguises() {
        return null;
    }
    private TreeMap<String, Object> getDuels() {
        return null;
    }
    private TreeMap<String, Object> getDungeons() {
        return null;
    }
    private TreeMap<String, Object> getEnvoy() {
        return null;
    }
    private TreeMap<String, Object> getFactionPoints() {
        return null;
    }
    private TreeMap<String, Object> getFactionQuests() {
        return null;
    }
    private TreeMap<String, Object> getFactionUpgrades() {
        return null;
    }
    private TreeMap<String, Object> getFund() {
        return null;
    }
    private TreeMap<String, Object> getGlobalChallenges() {
        return null;
    }
    private TreeMap<String, Object> getHomes() {
        return null;
    }
    private TreeMap<String, Object> getItemFilter() {
        return null;
    }
    private TreeMap<String, Object> getItems() {
        return null;
    }
    private TreeMap<String, Object> getJackpot() {
        return null;
    }
    private TreeMap<String, Object> getKits() {
        return null;
    }
    private TreeMap<String, Object> getKOTH() {
        return null;
    }
    private TreeMap<String, Object> getLastManStanding() {
        return null;
    }
    private TreeMap<String, Object> getLootboxes() {
        return null;
    }
    private TreeMap<String, Object> getMasks() {
        return null;
    }
    private TreeMap<String, Object> getMobStacker() {
        return null;
    }
    private TreeMap<String, Object> getMonthlyCrates() {
        return null;
    }
    private TreeMap<String, Object> getOutposts() {
        return null;
    }
    private TreeMap<String, Object> getPlayerQuests() {
        return null;
    }
    private TreeMap<String, Object> getSecondary() {
        return null;
    }
    private TreeMap<String, Object> getShop() {
        return null;
    }
    private TreeMap<String, Object> getShowcase() {
        return null;
    }
    private TreeMap<String, Object> getTitles() {
        return null;
    }
    private TreeMap<String, Object> getTrade() {
        return null;
    }
    private TreeMap<String, Object> getWildPvP() {
        return null;
    }


    private TreeMap<String, Object> newTree(Object...values) {
        int i = 0;
        final TreeMap<String, Object> tree = new TreeMap<>();
        for(Object o : values) {
            if(i%2 != 0) {
                tree.put((String) values[i-1], o);
            }
            i++;
        }
        return tree;
    }
    private List<String> newStringList(String...list) {
        return Arrays.asList(list);
    }
}