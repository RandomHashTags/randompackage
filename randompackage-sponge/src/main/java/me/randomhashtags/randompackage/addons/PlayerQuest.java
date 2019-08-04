package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifiable;

import java.util.List;

public interface PlayerQuest extends Identifiable {
    boolean isEnabled();
    String getName();
    long getExpiration();
    String getCompletion();
    boolean isTimeBased();
    double getTimedCompletion();
    List<String> getLore();
    List<String> getRewards();
    List<String> getTrigger();
}
