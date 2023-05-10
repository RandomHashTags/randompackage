package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.GlobalChallenge;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public final class FileGlobalChallenge extends RPAddonSpigot implements GlobalChallenge {
    private final boolean is_enabled;
    private final long duration;
    private final String type;
    private final List<String> attributes;
    private final ItemStack display;

    public FileGlobalChallenge(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        final JSONObject settings_json = parse_json_in_json(json, "settings");
        is_enabled = parse_boolean_in_json(settings_json, "enabled");
        if(is_enabled) {
            duration = parse_long_in_json(settings_json, "duration");
            type = parse_string_in_json(settings_json, "type");
            display = create_item_stack(json, "item");
            attributes = parse_list_string_in_json(json, "attributes");
            register(Feature.GLOBAL_CHALLENGE, this);
        } else {
            duration = 0;
            type = null;
            display = null;
            attributes = null;
        }
    }

    @Override
    public boolean isEnabled() {
        return is_enabled;
    }
    @NotNull
    @Override
    public ItemStack getItem() {
        return getClone(display);
    }
    @Override
    public long getDuration() {
        return duration;
    }
    @Override
    public String getType() {
        return type;
    }
    @Override
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
}
