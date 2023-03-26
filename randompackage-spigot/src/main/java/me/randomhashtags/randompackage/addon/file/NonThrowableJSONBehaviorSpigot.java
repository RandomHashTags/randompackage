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
        final Object obj = json.opt(key);
        final String string = obj instanceof String ? (String) obj : default_value;
        return string != null ? colorize(string) : null;
    }

    @NotNull
    default List<String> parse_list_string_in_json(@NotNull JSONObject json, @NotNull String key, @NotNull List<String> default_value) {
        final JSONArray array = json.optJSONArray(key);
        final List<String> list = array != null ? array.toList().stream().map(object -> Objects.toString(object, null)).collect(Collectors.toList()) : default_value;
        return colorizeListString(list);
    }

    @NotNull
    default MultilingualString parse_multilingual_string_in_json(@NotNull JSONObject json, @NotNull String key) {
        final JSONObject value = json.optJSONObject(key, new JSONObject());
        return new MultilingualStringSpigotValue(value);
    }
}