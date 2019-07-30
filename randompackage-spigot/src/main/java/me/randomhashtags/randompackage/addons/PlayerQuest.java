package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifyable;

import java.util.List;

public interface PlayerQuest extends Identifyable {
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
