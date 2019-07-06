package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifyable;
import me.randomhashtags.randompackage.addons.enums.OutpostStatus;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public abstract class Outpost extends Identifyable {
    public abstract String getName();
    public abstract int getSlot();
    public abstract ItemStack getDisplay();
    public abstract List<String> getLostControlMsg();
    public abstract List<String> getClaimedMsg();
    public abstract List<String> getLimits();
    public abstract List<String> getUnallowedItems();
    public abstract List<String> getRewards();
    public abstract Scoreboard getScoreboard();
    public abstract Location getWarpLocation();
    public abstract String getAttackingFaction();
    public abstract void setAttackingFaction(String faction);
    public abstract String getControllingFaction();
    public abstract void setControllingFaction(String faction);
    public abstract String getStatus();
    public abstract long getControlledStarting();
    public abstract double getControlPercent();
    public abstract OutpostStatus getOutpostStatus();
    public abstract void setOutpostStatus(OutpostStatus status);

    public static Outpost valueOf(int slot) {
        if(outposts != null) {
            for(Outpost o : outposts.values()) {
                if(o.getSlot() == slot) {
                    return o;
                }
            }
        }
        return null;
    }
}
