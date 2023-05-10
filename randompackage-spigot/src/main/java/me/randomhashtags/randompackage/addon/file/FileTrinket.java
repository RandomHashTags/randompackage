package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Trinket;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public final class FileTrinket extends RPAddonSpigot implements Trinket {
    private final boolean is_enabled, passive;
    private final long cooldown;
    private final ItemStack item;
    private final List<String> attributes;

    public FileTrinket(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        final JSONObject settings_json = parse_json_in_json(json, "settings");
        is_enabled = parse_boolean_in_json(settings_json, "enabled");
        if(is_enabled) {
            passive = parse_boolean_in_json(settings_json, "passive");
            cooldown = parse_long_in_json(settings_json, "cooldown");
            item = create_item_stack(json, "item");
            attributes = parse_list_string_in_json(json, "attributes");
            register(Feature.TRINKET, this);
        } else {
            passive = false;
            cooldown = 0;
            item = null;
            attributes = null;
        }
    }

    @Override
    public boolean isEnabled() {
        return is_enabled;
    }

    @Override
    public boolean isPassive() {
        return passive;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        return getClone(item);
    }
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
}
