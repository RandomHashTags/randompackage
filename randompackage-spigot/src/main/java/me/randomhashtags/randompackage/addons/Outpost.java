package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.enums.OutpostStatus;
import me.randomhashtags.randompackage.addons.utils.Captureable;
import me.randomhashtags.randompackage.addons.utils.Scoreboardable;
import org.bukkit.Location;

import java.util.List;

public interface Outpost extends Captureable, Scoreboardable {
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
    OutpostStatus getOutpostStatus();
    void setOutpostStatus(OutpostStatus status);
}
