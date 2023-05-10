package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.enums.Feature;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public final class FilePlayerQuest extends RPAddonSpigot implements PlayerQuest {

    private final boolean is_enabled;
    private final MultilingualString name;
    private final long expiration;
    private final String completion;
    private final List<String> lore, rewards, trigger;

    public FilePlayerQuest(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        final JSONObject settings_json = parse_json_in_json(json, "settings");
        is_enabled = parse_boolean_in_json(settings_json, "enabled");
        if(is_enabled) {
            name = parse_multilingual_string_in_json(settings_json, "name");
            expiration = parse_long_in_json(settings_json, "expiration");
            completion = parse_string_in_json(settings_json, "completion");
            lore = parse_list_string_in_json(json, "lore");
            rewards = parse_list_string_in_json(json, "rewards");
            trigger = parse_list_string_in_json(json, "trigger");
            register(Feature.PLAYER_QUEST, this);
        } else {
            name = null;
            expiration = 0;
            completion = null;
            lore = null;
            rewards = null;
            trigger = null;
        }
    }
    public boolean isEnabled() {
        return is_enabled;
    }
    public @NotNull MultilingualString getName() {
        return name;
    }
    public long getExpiration() {
        return expiration;
    }
    public @NotNull String getCompletion() {
        return completion;
    }
    public boolean isTimeBased() {
        final String completion_lowercase = getCompletion().toLowerCase();
        return completion_lowercase.contains("d") || completion_lowercase.contains("h") || completion_lowercase.contains("m") || completion_lowercase.contains("s");
    }
    public double getTimedCompletion() {
        String s = getCompletion().toLowerCase().replaceAll("\\p{Z}", "").replaceAll("\\p{S}", "").replaceAll("\\p{Nl}", "").replaceAll("\\p{No}", "").replaceAll("\\p{M}", "");
        double c = 0.00;
        if(s.contains("d")) {
            c += Double.parseDouble(s.split("d")[0])*24*60*60*1000;
            s = s.split("d")[1];
        }
        if(s.contains("h")) {
            c += Double.parseDouble(s.split("h")[0])*60*60*1000;
            s = s.split("h")[1];
        }
        if(s.contains("m")) {
            c += Double.parseDouble(s.split("m")[0])*60*1000;
            s = s.split("m")[1];
        }
        if(s.contains("s")) {
            c += Double.parseDouble(s.split("s")[0])*1000;
            //s = s.split("s")[1];
        }
        return c;
    }
    public @NotNull List<String> getLore() {
        return lore;
    }
    public @NotNull List<String> getRewards() {
        return rewards;
    }
    public @NotNull List<String> getTrigger() {
        return trigger;
    }
}
