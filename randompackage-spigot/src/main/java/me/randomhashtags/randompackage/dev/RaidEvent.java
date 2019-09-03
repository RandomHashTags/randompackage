package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addons.utils.Rewardable;
import me.randomhashtags.randompackage.addons.utils.Scheduleable;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface RaidEvent extends Rewardable, Scheduleable {
    Location getLocation();

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
