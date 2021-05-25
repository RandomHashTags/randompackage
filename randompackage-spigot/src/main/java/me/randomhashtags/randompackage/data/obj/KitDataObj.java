package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.data.KitData;

import java.util.HashMap;

public final class KitDataObj implements KitData {
    private final HashMap<CustomKit, Long> cooldowns;
    private final HashMap<CustomKit, Integer> levels;

    public KitDataObj(HashMap<CustomKit, Long> cooldowns, HashMap<CustomKit, Integer> levels) {
        this.cooldowns = cooldowns;
        this.levels = levels;
    }

    @Override
    public HashMap<CustomKit, Integer> getLevels() {
        return levels;
    }

    @Override
    public HashMap<CustomKit, Long> getCooldowns() {
        return cooldowns;
    }
}
