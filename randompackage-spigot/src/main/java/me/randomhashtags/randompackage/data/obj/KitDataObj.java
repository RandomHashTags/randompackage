package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.data.KitData;

import java.util.HashMap;

public class KitDataObj implements KitData {
    private HashMap<CustomKit, Long> cooldowns;
    private HashMap<CustomKit, Integer> levels;

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
