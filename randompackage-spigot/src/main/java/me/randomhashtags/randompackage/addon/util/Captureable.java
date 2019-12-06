package me.randomhashtags.randompackage.addon.util;

import me.randomhashtags.randompackage.enums.CaptureType;
import me.randomhashtags.randompackage.util.obj.PolyBoundary;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public interface Captureable extends Itemable, Rewardable {
    long getStartTime();
    boolean allowsAFKCapturing();

    Location getCenter();
    CaptureType getCaptureType();
    PolyBoundary getSquareCaptureZone();
    double getCaptureRadius();
    long getCaptureTime();
    OfflinePlayer getCapturer();

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

    String getStatus();
    HashMap<String, String> getStatuses();

    double getControllingPercent();

    List<String> getCapturedMsg();
    List<String> getNoLongerControllingMsg();
    List<String> getTakenControlMsg();
}
