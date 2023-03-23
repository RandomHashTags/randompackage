package me.randomhashtags.randompackage.dev.dungeons;

import me.randomhashtags.randompackage.dev.Dungeon;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public abstract class FileDungeon extends RPAddonSpigot implements Dungeon {
    private ItemStack display, key, keyLocked, portal, lootbag;
    private long fastestCompletion;

    public FileDungeon(File f) {
        super(f);
        register(Feature.DUNGEON, this);
    }

    public int getSlot() { return yml.getInt("gui.slot"); }
    public @NotNull ItemStack getItem() {
        if(display == null) display = createItemStack(yml, "gui");
        return getClone(display);
    }
    public ItemStack getKey() {
        if(key == null) key = createItemStack(yml, "key");
        return getClone(key);
    }
    public ItemStack getKeyLocked() {
        if(keyLocked == null) keyLocked = createItemStack(yml, "gui.key locked");
        return getClone(keyLocked);
    }
    public ItemStack getPortal() {
        if(portal == null) portal = createItemStack(yml, "portal");
        return getClone(portal);
    }
    public ItemStack getLootbag() {
        if(lootbag == null) lootbag = createItemStack(yml, "lootbag");
        return getClone(lootbag);
    }
    public List<String> getLootbagRewards() {
        return yml.getStringList("lootbag.rewards");
    }

    public Location getTeleportLocation() { return string_to_location(yml.getString("settings.warp location")); }
    public long getFastestCompletion() { return fastestCompletion; }
    public void setFastestCompletion(long fastestCompletion) { this.fastestCompletion = fastestCompletion; }
}
