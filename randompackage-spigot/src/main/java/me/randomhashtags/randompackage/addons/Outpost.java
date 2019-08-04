package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifiable;
import me.randomhashtags.randompackage.addons.enums.OutpostStatus;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public interface Outpost extends Identifiable {
    String getName();
    int getSlot();
    ItemStack getDisplay();
    List<String> getLostControlMsg();
    List<String> getClaimedMsg();
    List<String> getLimits();
    List<String> getUnallowedItems();
    List<String> getRewards();
    Scoreboard getScoreboard();
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
