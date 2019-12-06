package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.enums.OutpostStatus;
import me.randomhashtags.randompackage.addon.util.Captureable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.addon.util.Scoreboardable;
import me.randomhashtags.randompackage.addon.util.Slotable;
import org.bukkit.Location;

import java.util.List;

public interface Outpost extends Captureable, Nameable, Slotable, Scoreboardable {
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
