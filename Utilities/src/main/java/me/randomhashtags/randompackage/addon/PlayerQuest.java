package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.addon.util.Rewardable;
import me.randomhashtags.randompackage.addon.util.Toggleable;

import java.util.List;

public interface PlayerQuest extends Nameable, Rewardable, Toggleable {
    long getExpiration();
    String getCompletion();
    boolean isTimeBased();
    double getTimedCompletion();
    List<String> getLore();
    List<String> getTrigger();
}
