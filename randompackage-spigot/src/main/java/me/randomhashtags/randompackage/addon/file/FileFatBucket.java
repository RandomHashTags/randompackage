package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.FatBucket;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public final class FileFatBucket extends RPAddonSpigot implements FatBucket {
    private final ItemStack bucket;
    private final int uses, sources_required;
    private final String uses_left_status, percent_full_status;
    private final List<String> enabled_worlds, fillable_in_worlds, only_fillable_in_worlds_message;
    public FileFatBucket(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        bucket = create_item_stack(json, "item");
        final JSONObject settings_json = json.getJSONObject("settings");
        uses = parse_int_in_json(settings_json, "uses");
        sources_required = parse_int_in_json(settings_json, "sources required");
        final JSONObject status_json = settings_json.getJSONObject("status");
        uses_left_status = parse_string_in_json(status_json, "uses left");
        percent_full_status = parse_string_in_json(status_json, "percent full");
        enabled_worlds = parse_list_string_in_json(settings_json, "enabled worlds");
        fillable_in_worlds = parse_list_string_in_json(settings_json, "fillable in worlds");
        final JSONObject messages_json = json.getJSONObject("messages");
        only_fillable_in_worlds_message = parse_list_string_in_json(messages_json, "only fillable in worlds");
        register(Feature.FAT_BUCKET, this);
    }

    @NotNull
    public ItemStack getItem() {
        return getClone(bucket);
    }
    public int getUses() {
        return uses;
    }
    public int getSourcesRequired() {
        return sources_required;
    }
    public String getUsesLeftStatus() {
        return uses_left_status;
    }
    public String getPercentFullStatus() {
        return percent_full_status;
    }
    public List<String> getEnabledWorlds() {
        return enabled_worlds;
    }
    public List<String> getFillableInWorlds() {
        return fillable_in_worlds;
    }
    public List<String> getOnlyFillableInWorldsMsg() {
        return only_fillable_in_worlds_message;
    }
}
