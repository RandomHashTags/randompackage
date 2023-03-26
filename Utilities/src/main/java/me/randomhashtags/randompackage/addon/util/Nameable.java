package me.randomhashtags.randompackage.addon.util;

import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.util.Language;
import org.jetbrains.annotations.NotNull;

public interface Nameable {
    @NotNull
    MultilingualString getName();

    @NotNull
    default String getName(Language language) {
        return getName().get(language);
    }
}
