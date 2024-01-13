package me.randomhashtags.randompackage.addon.dev;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface DungeonStats {
    long getFastestCompletion();
    Date getFastestCompletedDate();
    List<UUID> getCompletedBy();
}
