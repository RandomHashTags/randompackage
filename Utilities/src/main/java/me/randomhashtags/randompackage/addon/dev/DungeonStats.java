package me.randomhashtags.randompackage.addon.dev;

import java.util.Date;
import java.util.List;

public interface DungeonStats {
    long getFastestCompletion();
    Date getFastestCompletedDate();
    List<String> getCompletedBy();
}
