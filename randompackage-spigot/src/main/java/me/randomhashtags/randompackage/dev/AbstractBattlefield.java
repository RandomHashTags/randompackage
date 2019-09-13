package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.util.addon.RPAddon;
import org.bukkit.Location;

import java.util.List;

public abstract class AbstractBattlefield extends RPAddon implements BattlefieldEvent {
    public Location getWarpLocation() { return toLocation(yml.getString("settings.warp location")); }
    public List<String> getAllowedCommands() { return yml.getStringList("allowed commands"); }

    public List<String> getStartedMsg() { return colorizeListString(yml.getStringList("messages.started")); }
}
