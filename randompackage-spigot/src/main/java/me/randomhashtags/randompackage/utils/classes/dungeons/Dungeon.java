package me.randomhashtags.randompackage.utils.classes.dungeons;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Dungeon {
    public static HashMap<String, Dungeon> dungeons;
    private static RandomPackageAPI api;

    private long fastestCompletion;
    private YamlConfiguration yml;
    private String ymlName;
    private int slot;
    private ItemStack display, key, keyLocked, lootbag;
    private List<String> lootbagRewards;
    private Location teleportLocation;
    public Dungeon(File f) {
        if(dungeons == null) {
            dungeons = new HashMap<>();
            api = RandomPackageAPI.getAPI();
        }
        yml = YamlConfiguration.loadConfiguration(f);
        ymlName = f.getName().split("\\.yml")[0];
        slot = yml.getInt("gui.slot");
        dungeons.put(ymlName, this);
    }
    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() { return ymlName; }

    public int getSlot() { return slot; }
    public ItemStack getDisplay() {
        if(display == null) display = api.d(yml, "gui");
        return display.clone();
    }
    public ItemStack getKey() {
        if(key == null) key = api.d(yml, "key");
        return key.clone();
    }
    public ItemStack getKeyLocked() {
        if(keyLocked == null) keyLocked = api.d(yml, "gui.key locked");
        return keyLocked.clone();
    }
    public ItemStack getLootbag() {
        if(lootbag == null) lootbag = api.d(yml, "lootbag");
        return lootbag.clone();
    }
    public List<String> getLootbagRewards() {
        if(lootbagRewards == null) lootbagRewards = yml.getStringList("lootbag.rewards");
        return lootbagRewards;
    }


    public static Dungeon valueOf(ItemStack key) {
        if(dungeons != null && key != null) {
            for(Dungeon d : dungeons.values()) {
                if(d.getKey().isSimilar(key)) {
                    return d;
                }
            }
        }
        return null;
    }
    public static void deleteAll() {
        dungeons = null;
        api = null;
    }
}
