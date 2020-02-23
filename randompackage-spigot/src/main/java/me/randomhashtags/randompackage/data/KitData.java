package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.CustomKit;

import java.util.HashMap;

public interface KitData {
    HashMap<CustomKit, Integer> getLevels();
    default int getLevel(@NotNull CustomKit kit) {
        final HashMap<CustomKit, Integer> levels = getLevels();
        return levels != null ? levels.getOrDefault(kit, -1) : -1;
    }
    HashMap<CustomKit, Long> getCooldowns();
    default long getCooldown(@NotNull CustomKit kit) {
        final HashMap<CustomKit, Long> cooldowns = getCooldowns();
        return cooldowns != null ? cooldowns.getOrDefault(kit, -1l) : -1;
    }
}
