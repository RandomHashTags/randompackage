package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.enums.OutpostStatus;
import me.randomhashtags.randompackage.addon.util.Captureable;
import me.randomhashtags.randompackage.addon.util.Scoreboardable;
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
