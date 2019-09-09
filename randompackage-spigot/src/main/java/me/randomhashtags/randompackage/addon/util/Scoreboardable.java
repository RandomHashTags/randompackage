package me.randomhashtags.randompackage.addon.util;

import org.bukkit.scoreboard.Scoreboard;

public interface Scoreboardable extends Identifiable {
    Scoreboard getScoreboard();
    int getScoreboardRadius();
    int getScoreboardUpdateInterval();
}
