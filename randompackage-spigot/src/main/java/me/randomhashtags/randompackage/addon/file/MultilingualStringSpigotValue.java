package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import me.randomhashtags.randompackage.util.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.*;

public final class MultilingualStringSpigotValue implements MultilingualString, UVersionableSpigot {

    private final HashMap<Language, String> values;

    public MultilingualStringSpigotValue(@Nullable JSONObject json) {
        values = new HashMap<>();
        if(json != null) {
            final List<Language> supported_languages = List.of(
                    Language.ENGLISH,
                    Language.SPANISH
            );
            for(Language language : supported_languages) {
                String string;
                try {
                    string = json.getString(language.code);
                    string = colorize(string);
                } catch (Exception ignored) {
                    string = null; // TODO: print to console
                }
                values.put(language, string);
            }
        }
    }
    public MultilingualStringSpigotValue(@NotNull String english) {
        values = new HashMap<>();
        values.put(Language.ENGLISH, english);
    }

    public @NotNull String get(@NotNull Language language) {
        return values.getOrDefault(language, "null");
    }
}
