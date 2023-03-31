package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.addon.util.Rewardable;
import me.randomhashtags.randompackage.addon.util.Toggleable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PlayerQuest extends Nameable, Rewardable, Toggleable {
    long getExpiration();
    @NotNull String getCompletion();
    boolean isTimeBased();
    double getTimedCompletion();
    @NotNull List<String> getLore();
    @NotNull List<String> getTrigger();
}
