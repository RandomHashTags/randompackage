package me.randomhashtags.randompackage.util;

import org.jetbrains.annotations.NotNull;

public enum Language {
    ENGLISH("en"),
    SPANISH("es"),
    ;

    @NotNull public final String code;

    Language(@NotNull String code) {
        this.code = code;
    }
}
