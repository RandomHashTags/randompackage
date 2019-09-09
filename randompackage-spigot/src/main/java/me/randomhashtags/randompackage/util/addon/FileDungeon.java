package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.dev.Dungeon;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public abstract class FileDungeon extends RPAddon implements Dungeon {
    private ItemStack display, key, keyLocked, portal, lootbag;
    private long fastestCompletion;

    public FileDungeon(File f) {
        load(f);
        addDungeon(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public int getSlot() { return yml.getInt("gui.slot"); }
    public ItemStack getItem() {
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
    public ItemStack getPortal() {
        if(portal == null) portal = api.d(yml, "portal");
        return portal.clone();
    }
    public ItemStack getLootbag() {
        if(lootbag == null) lootbag = api.d(yml, "lootbag");
        return lootbag.clone();
    }
    public List<String> getLootbagRewards() {
        return yml.getStringList("lootbag.rewards");
    }

    public Location getTeleportLocation() { return api.toLocation(yml.getString("settings.warp location")); }
    public long getFastestCompletion() { return fastestCompletion; }
    public void setFastestCompletion(long fastestCompletion) { this.fastestCompletion = fastestCompletion; }
}
