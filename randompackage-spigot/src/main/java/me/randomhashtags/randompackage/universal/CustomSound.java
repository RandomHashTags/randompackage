package me.randomhashtags.randompackage.universal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CustomSound {
    private final USound usound;
    private final float volume, pitch;
    public CustomSound(@NotNull String input) {
        final String[] values = input.split(":");
        usound = USound.match(values[0].toUpperCase());
        volume = Float.parseFloat(values[1]);
        pitch = Float.parseFloat(values[2]);
    }
    @Nullable
    public USound getUSound() {
        return usound;
    }
    public float getVolume() {
        return volume;
    }
    public float getPitch() {
        return pitch;
    }
}
