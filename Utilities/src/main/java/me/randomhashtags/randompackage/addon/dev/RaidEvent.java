package me.randomhashtags.randompackage.addon.dev;

import me.randomhashtags.randompackage.addon.util.Rewardable;
import me.randomhashtags.randompackage.addon.util.Scheduleable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface RaidEvent extends Rewardable, Scheduleable {
    int getMaxAdjacentFactionClaims();
    BigDecimal getRewardingFactionPoints();

    String getStatus();
    String getPhase();
    HashMap<String, String> getPhases();
    int getPlayers();

    long getAllowedLootTime();
    long getAllowedEscapeTime();

    List<String> getInfoMsg();
    List<String> getWinnerMsg();
    List<String> getLootedMsg();
    List<String> getWillBeDestroyedInMsg();
}
