package me.randomhashtags.randompackage.addon.util;

import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public interface Scoreboardable extends Identifiable {
    Scoreboard getScoreboard();
    List<String> getScores();
    int getScoreboardRadius();
    int getScoreboardUpdateInterval();
}
