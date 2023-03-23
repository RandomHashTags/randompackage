package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import org.bukkit.Location;

import java.util.List;

public abstract class AbstractBattlefield extends RPAddonSpigot implements BattlefieldEvent {
    public Location getWarpLocation() { return string_to_location(yml.getString("settings.warp location")); }
    public List<String> getAllowedCommands() { return yml.getStringList("allowed commands"); }

    public List<String> getStartedMsg() { return colorizeListString(yml.getStringList("messages.started")); }
}
