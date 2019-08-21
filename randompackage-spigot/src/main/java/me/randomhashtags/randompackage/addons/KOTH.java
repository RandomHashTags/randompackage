package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.enums.CaptureType;
import me.randomhashtags.randompackage.addons.utils.Rewardable;
import me.randomhashtags.randompackage.addons.utils.Scheduleable;
import me.randomhashtags.randompackage.addons.utils.Scoreboardable;
import me.randomhashtags.randompackage.utils.objects.PolyBoundary;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;

public interface KOTH extends Rewardable, Scheduleable, Scoreboardable {
    String getName();
    List<String> getAllowedCommands();
    CaptureType getCaptureType();
    PolyBoundary getSquareCaptureZone();
    double getCaptureRadius();
    long getCaptureTime();

    default double getPlayerDistance(Player player) {
        final Location l = player.getLocation(), c = getCenter();
        switch (getCaptureType()) {
            case CIRCLE:
                return l.distance(c);
            case SQUARE:
                final PolyBoundary b = getSquareCaptureZone();
                if(b.contains(l)) {
                    return 0.00;
                } else {
                    // TODO
                    return -1;
                }
            default:
                return 0.00;
        }
    }

    boolean allowsAFKCapturing();

    Location getCenter();
    long getStartTime();
    String getStatus();
    String getFlag();
    HashMap<String, String> getStatuses();
    HashMap<String, String> getFlags();

    int getScoreboardRadius();
    int getScoreboardUpdateInterval();
    int getScoreboardStartNumber();
    Scoreboard getCapturedScoreboard();

    List<KOTHMonster> getMonsters();

    ItemStack getLootbag();
    UInventory getLootbagGUI();
    List<String> getLootbagRewards();

    List<String> getJoinMsg();
    List<String> getInfoMsg();
    List<String> getNoEventRunningMsg();
    List<String> getAlreadyCapturedMsg();
    List<String> getEventRunningMsg();
    List<String> getStartingInMsg();
    List<String> getMonstersSpawnedMsg();
    List<String> getStartCapturingMsg();
    List<String> getNoLongerCapturingMsg();
    List<String> getBlockedCommandMsg();
    List<String> getTeleportMsg();
    List<String> getCapturingCountdowns();
    List<String> getCapturingMsg();
    List<String> getCaptureMsg();
    List<String> getSetCenterMsg();

    String getRewardFormat();
    List<String> getOpenLootbagMsg();
}
