package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import me.randomhashtags.randompackage.util.NonThrowableJSONBehavior;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface NonThrowableJSONBehaviorSpigot extends UVersionableSpigot, NonThrowableJSONBehavior {

    @Nullable
    default String parse_string_in_json(@NotNull JSONObject json, @NotNull String key, @Nullable String default_value) {
        final String string = json.optString(key);
        if(string == null) { // TODO: print to console
            return default_value != null ? colorize(default_value) : null;
        }
        return colorize(string);
    }

    @NotNull
    default List<String> parse_list_string_in_json(@NotNull JSONObject json, @NotNull String key, @NotNull List<String> default_value) {
        final JSONArray array = json.optJSONArray(key);
        if(array == null) { // TODO: print to console
            return default_value;
        }
        final List<String> list = array.toList().stream().map(object -> Objects.toString(object, null)).collect(Collectors.toList());
        return colorizeListString(list);
    }

    @NotNull
    default MultilingualString parse_multilingual_string_in_json(@NotNull JSONObject json, @NotNull String key) {
        final JSONObject value = json.optJSONObject(key);
        if(value == null) { // TODO: print to console
            return new MultilingualStringSpigotValue((JSONObject) null);
        }
        return new MultilingualStringSpigotValue(value);
    }
}