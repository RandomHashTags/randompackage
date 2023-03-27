package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.addon.enums.BoosterRecipients;
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

    default double parse_double_in_json(@NotNull JSONObject json, @NotNull String key) {
        return parse_double_in_json(json, key, 0);
    }
    default double parse_double_in_json(@NotNull JSONObject json, @NotNull String key, double default_value) {
        final Object obj = json.opt(key);
        return obj instanceof Double ? (Double) obj : default_value;
    }

    @NotNull
    default String parse_string_in_json(@NotNull JSONObject json, @NotNull String key) {
        return parse_string_in_json(json, key, "");
    }
    @Nullable
    String parse_string_in_json(@NotNull JSONObject json, @NotNull String key, @Nullable String default_value);

    @NotNull
    MultilingualString parse_multilingual_string_in_json(@NotNull JSONObject json, @NotNull String key);

    @NotNull
    default BoosterRecipients parse_booster_recipients_in_json(@NotNull JSONObject json, @NotNull String key) {
        final String target = parse_string_in_json(json, key);
        try {
            return BoosterRecipients.valueOf(target.toUpperCase());
        } catch (Exception e) { // TODO: print to console
            return BoosterRecipients.SELF;
        }
    }

    @NotNull
    default BigDecimal parse_big_decimal_in_json(@NotNull JSONObject json, @NotNull String key) {
        final double value = parse_double_in_json(json, key);
        return BigDecimal.valueOf(value);
    }

    @NotNull
    default List<String> parse_list_string_in_json(@NotNull JSONObject json, @NotNull String key) {
        return parse_list_string_in_json(json, key, List.of());
    }

    @NotNull
    List<String> parse_list_string_in_json(@NotNull JSONObject json, @NotNull String key, @NotNull List<String> default_value);

    @NotNull
    default List<BigDecimal> parse_list_big_decimal_in_json(@NotNull JSONObject json, @NotNull String key) {
        return parse_list_big_decimal_in_json(json, key, List.of());
    }
    @Nullable
    default List<BigDecimal> parse_list_big_decimal_in_json(@NotNull JSONObject json, @NotNull String key, @Nullable List<BigDecimal> default_value) {
        final JSONArray array = json.optJSONArray(key);
        return array != null ? array.toList().stream().map(object -> BigDecimal.valueOf(Long.parseLong(Objects.toString(object, "0")))).collect(Collectors.toList()) : default_value;
    }
}
