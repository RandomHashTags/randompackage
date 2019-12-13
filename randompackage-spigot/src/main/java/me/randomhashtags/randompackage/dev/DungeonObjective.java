package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.util.Scoreboardable;

import java.util.HashMap;
import java.util.List;

public interface DungeonObjective extends Scoreboardable {
    long getTimeLimit();
    HashMap<String, List<String>> getMessages();
}
