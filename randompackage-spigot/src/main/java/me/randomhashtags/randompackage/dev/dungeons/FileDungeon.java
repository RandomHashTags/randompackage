package me.randomhashtags.randompackage.dev.dungeons;

import me.randomhashtags.randompackage.dev.Dungeon;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public abstract class FileDungeon extends RPAddonSpigot implements Dungeon {
    private final int slot;
    private final ItemStack display, key, key_locked, portal, lootbag;
    private final List<String> lootbag_rewards;
    private final Location warp_location;
    private long fastestCompletion;

    public FileDungeon(File f) {
        super(f);

        final JSONObject json = parse_json_from_file(f);
        display = create_item_stack(json, "gui");
        key = create_item_stack(json, "key");
        portal = create_item_stack(json, "portal");

        final JSONObject lootbag_json = json.getJSONObject("lootbag");
        lootbag = create_item_stack(json, "lootbag");
        lootbag_rewards = parse_list_string_in_json(lootbag_json, "rewards");

        final JSONObject gui_json = json.getJSONObject("gui");
        slot = parse_int_in_json(gui_json, "slot");
        key_locked = create_item_stack(gui_json, "key locked");

        final JSONObject settings_json = json.getJSONObject("settings");
        warp_location = string_to_location(parse_string_in_json(settings_json, "warp location"));

        register(Feature.DUNGEON, this);
    }

    @Override
    public int getSlot() {
        return slot;
    }
    public @NotNull ItemStack getItem() {
        return getClone(display);
    }
    public ItemStack getKey() {
        return getClone(key);
    }
    public ItemStack getKeyLocked() {
        return getClone(key_locked);
    }
    public ItemStack getPortal() {
        return getClone(portal);
    }
    public ItemStack getLootbag() {
        return getClone(lootbag);
    }
    public List<String> getLootbagRewards() {
        return lootbag_rewards;
    }

    public Location getTeleportLocation() {
        return warp_location;
    }
    public long getFastestCompletion() {
        return fastestCompletion;
    }
    public void setFastestCompletion(long fastestCompletion) {
        this.fastestCompletion = fastestCompletion;
    }
}
