package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.addon.dev.FactionQuest;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import me.randomhashtags.randompackage.enums.Feature;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class FileFactionQuest extends RPAddonSpigot implements FactionQuest {
    private final MultilingualString name;
    private final BigDecimal completion;
    private final List<String> lore, rewards, attributes;
    public FileFactionQuest(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        lore = parse_list_string_in_json(json, "lore");
        rewards = parse_list_string_in_json(json, "rewards");
        attributes = parse_list_string_in_json(json, "attributes");
        final JSONObject settings_json = json.getJSONObject("settings");
        name = parse_multilingual_string_in_json(settings_json, "name");
        completion = parse_big_decimal_in_json(settings_json, "completion");
        register(Feature.FACTION_QUEST, this);
    }
    public @NotNull MultilingualString getName() {
        return name;
    }
    public @NotNull BigDecimal getCompletion() {
        return completion;
    }

    public @NotNull List<String> getLore() {
        return lore;
    }
    public @NotNull List<String> getRewards() {
        return rewards;
    }
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
}
