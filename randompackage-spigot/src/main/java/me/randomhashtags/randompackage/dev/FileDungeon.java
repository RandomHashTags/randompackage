package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.util.addon.RPAddon;
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
        return getClone(display);
    }
    public ItemStack getKey() {
        if(key == null) key = api.d(yml, "key");
        return getClone(key);
    }
    public ItemStack getKeyLocked() {
        if(keyLocked == null) keyLocked = api.d(yml, "gui.key locked");
        return getClone(keyLocked);
    }
    public ItemStack getPortal() {
        if(portal == null) portal = api.d(yml, "portal");
        return getClone(portal);
    }
    public ItemStack getLootbag() {
        if(lootbag == null) lootbag = api.d(yml, "lootbag");
        return getClone(lootbag);
    }
    public List<String> getLootbagRewards() {
        return yml.getStringList("lootbag.rewards");
    }

    public Location getTeleportLocation() { return toLocation(yml.getString("settings.warp location")); }
    public long getFastestCompletion() { return fastestCompletion; }
    public void setFastestCompletion(long fastestCompletion) { this.fastestCompletion = fastestCompletion; }
}
