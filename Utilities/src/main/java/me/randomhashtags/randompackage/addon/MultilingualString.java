package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.util.Language;
import org.jetbrains.annotations.NotNull;

public interface MultilingualString {
    @NotNull String get(@NotNull Language language);
}
