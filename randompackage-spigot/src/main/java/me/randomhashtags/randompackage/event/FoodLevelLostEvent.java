package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;

public final class FoodLevelLostEvent extends RPEventCancellable {
    private final int level, newLevel;
    public FoodLevelLostEvent(Player player, int level, int newLevel) {
        super(player);
        this.level = level;
        this.newLevel = newLevel;
    }
    public int getLevel() {
        return level;
    }
    public int getNewLevel() {
        return newLevel;
    }
}
