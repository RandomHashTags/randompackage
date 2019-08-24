package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Rewardable;
import me.randomhashtags.randompackage.addons.utils.Toggleable;

import java.util.List;

public interface PlayerQuest extends Rewardable, Toggleable {
    String getName();
    long getExpiration();
    String getCompletion();
    boolean isTimeBased();
    double getTimedCompletion();
    List<String> getLore();
    List<String> getTrigger();
}
