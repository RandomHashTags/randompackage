package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.enums.OutpostStatus;
import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.addons.utils.Rewardable;
import me.randomhashtags.randompackage.addons.utils.Scoreboardable;
import org.bukkit.Location;

import java.util.List;

public interface Outpost extends Itemable, Rewardable, Scoreboardable {
    String getName();
    int getSlot();
    List<String> getLostControlMsg();
    List<String> getClaimedMsg();
    List<String> getLimits();
    List<String> getUnallowedItems();
    Location getWarpLocation();
    String getAttackingFaction();
    void setAttackingFaction(String faction);
    String getControllingFaction();
    void setControllingFaction(String faction);
    String getStatus();
    long getControlledStarting();
    double getControlPercent();
    OutpostStatus getOutpostStatus();
    void setOutpostStatus(OutpostStatus status);
}
