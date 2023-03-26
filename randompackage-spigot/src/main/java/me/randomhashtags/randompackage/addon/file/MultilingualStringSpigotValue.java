package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import me.randomhashtags.randompackage.util.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.HashMap;

public final class MultilingualStringSpigotValue implements MultilingualString, UVersionableSpigot {

    private final HashMap<Language, String> values;

    public MultilingualStringSpigotValue(@Nullable JSONObject json) {
        values = new HashMap<>();
        if (json != null) {
            String english;
            try {
                english = json.getString("en");
                english = colorize(english);
            } catch (Exception ignored) {
                english = null;
            }

            String spanish;
            try {
                spanish = json.getString("es");
                spanish = colorize(spanish);
            } catch (Exception ignored) {
                spanish = null;
            }

            values.put(Language.ENGLISH, english);
            values.put(Language.SPANISH, spanish);
        }
    }

    public @NotNull String get(@NotNull Language language) {
        return values.getOrDefault(language, "null");
    }
}
