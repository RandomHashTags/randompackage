package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.NamespacedKey;
import me.randomhashtags.randompackage.utils.abstraction.AbstractDungeon;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class Dungeon extends AbstractDungeon {

    private long fastestCompletion;
    private ItemStack display, key, keyLocked, lootbag;

    public Dungeon(File f) {
        load(f);
        created(new NamespacedKey(getPlugin, getYamlName()));
    }

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
