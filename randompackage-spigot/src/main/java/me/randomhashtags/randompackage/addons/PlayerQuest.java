package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifiable;
import me.randomhashtags.randompackage.addons.utils.Rewardable;
import me.randomhashtags.randompackage.addons.utils.Toggleable;

import java.util.List;

public interface PlayerQuest extends Identifiable, Rewardable, Toggleable {
    String getName();
    long getExpiration();
    String getCompletion();
    boolean isTimeBased();
    double getTimedCompletion();
    List<String> getLore();
    List<String> getTrigger();
}
