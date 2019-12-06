package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.Outpost;
import me.randomhashtags.randompackage.enums.OutpostStatus;
import me.randomhashtags.randompackage.addon.file.RPAddon;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.List;

// TODO: fix class
public abstract class FileOutpost extends RPAddon implements Outpost {
    private ItemStack item;
    private List<String> lostControl, claimed;
    private Scoreboard scoreboard;

    private String attacking, controlling, status;
    private OutpostStatus statusType;
    private long controlledStarting;
    private double controlPercent;
    public FileOutpost(File f) {
        load(f);
        addOutpost(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String getName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("settings.name")); }
    public int getSlot() { return yml.getInt("gui.slot"); }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "gui");
        return getClone(item);
    }
    public List<String> getLostControlMsg() {
        if(lostControl == null) lostControl = colorizeListString(yml.getStringList("messages.lost control"));
        return lostControl;
    }
    public List<String> getClaimedMsg() {
        if(claimed == null) claimed = colorizeListString(yml.getStringList("messages.claimed"));
        return claimed;
    }
    public List<String> getLimits() { return yml.getStringList("limits"); }
    public List<String> getUnallowedItems() { return yml.getStringList("unallowed items"); }
    public List<String> getRewards() { return yml.getStringList("rewards"); }
    public Scoreboard getScoreboard() {
        if(scoreboard == null) {
            scoreboard = SCOREBOARD_MANAGER.getNewScoreboard();
            final Objective o = scoreboard.registerNewObjective("dummy", "dummy");
            o.setDisplayName(ChatColor.translateAlternateColorCodes('&', yml.getString("scoreboard.title")));
            final int s = yml.getInt("scoreboard.score start");
            int i = 0;
            for(String a : yml.getStringList("scoreboard.scores")) {
                o.getScore(ChatColor.translateAlternateColorCodes('&', a)).setScore(s-i);
                i++;
            }
        }
        return scoreboard;
    }
    public Location getWarpLocation() { return toLocation(yml.getString("settings.warp location")); }

    public String getAttackingFaction() { return attacking; }
    public void setAttackingFaction(String faction) { attacking = faction; }
    public String getControllingFaction() { return controlling; }
    public void setControllingFaction(String faction) { controlling = faction; }
    public String getStatus() {
        final String a = attacking, c = controlling;
        return status.replace("{CAP%}", Double.toString(round(controlPercent, 4))).replace("{ATTACKING_FACTION}", a != null ? a : "N/A").replace("{CONTROLLING_FACTION}", c != null ? c : "N/A");
    }
    public long getControlledStarting() { return controlledStarting; }
    public double getControlPercent() { return controlPercent; }
    public OutpostStatus getOutpostStatus() { return statusType; }
    public void setOutpostStatus(OutpostStatus type) {
        statusType = type;
        status = getStatuses().get(type.name());
    }
}
