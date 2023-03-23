package me.randomhashtags.randompackage.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface NonThrowableJSONBehavior {

    @NotNull
    default JSONObject parse_json_from_file(@NotNull File file) {
        try {
            final JSONTokener tokener = new JSONTokener(new FileInputStream(file));
            return new JSONObject(tokener);
        } catch (Exception ignored) {
            return new JSONObject();
        }
    }
    default boolean parse_boolean_in_json(@NotNull JSONObject json, @NotNull String key) {
        final Object obj = json.opt(key);
        return obj instanceof Boolean && (Boolean) obj;
    }

    default int parse_int_in_json(@NotNull JSONObject json, @NotNull String key) {
        return parse_int_in_json(json, key, 0);
    }
    default int parse_int_in_json(@NotNull JSONObject json, @NotNull String key, int default_value) {
        final Object obj = json.opt(key);
        return obj instanceof Integer ? (Integer) obj : default_value;
    }

    @NotNull
    default String parse_string_in_json(@NotNull JSONObject json, @NotNull String key) {
        return parse_string_in_json(json, key, "");
    }
    @Nullable
    default String parse_string_in_json(@NotNull JSONObject json, @NotNull String key, @Nullable String default_value) {
        final Object obj = json.opt(key);
        return obj instanceof String ? (String) obj : default_value;
    }

    @NotNull
    default List<String> parse_list_string_in_json(@NotNull JSONObject json, @NotNull String key) {
        final JSONArray array = json.optJSONArray(key);
        return array != null ? array.toList().stream().map(object -> Objects.toString(object, null)).collect(Collectors.toList()) : List.of();
    }

    @NotNull
    default List<BigDecimal> parse_big_decimal_in_json(@NotNull JSONObject json, @NotNull String key) {
        final JSONArray array = json.optJSONArray(key);
        return array != null ? array.toList().stream().map(object -> BigDecimal.valueOf(Long.parseLong(Objects.toString(object, "0")))).collect(Collectors.toList()) : List.of();
    }
}
