package me.randomhashtags.randompackage.recode.api.addons.usingFile;

import me.randomhashtags.randompackage.recode.api.addons.Dungeon;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileDungeon extends Dungeon {
    private ItemStack display, key, keyLocked, lootbag;
    private long fastestCompletion;

    public FileDungeon(File f) {
        load(f);
        initilize();
    }
    public void initilize() { addDungeon(getYamlName(), this); }

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
        return yml.getStringList("lootbag.rewards");
    }

    public int getSlot() { return yml.getInt("gui.slot"); }
    public Location getTeleportLocation() { return api.toLocation(yml.getString("settings.warp location")); }
    public long getFastestCompletion() { return fastestCompletion; }
    public void setFastestCompletion(long fastestCompletion) { this.fastestCompletion = fastestCompletion; }
}
