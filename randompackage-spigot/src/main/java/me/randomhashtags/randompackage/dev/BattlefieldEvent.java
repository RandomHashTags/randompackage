package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.util.Scheduleable;
import me.randomhashtags.randompackage.addon.util.Scoreboardable;
import org.bukkit.Location;

import java.util.List;

public interface BattlefieldEvent extends Scheduleable, Scoreboardable {
    Location getWarpLocation();
    List<String> getAllowedCommands();

    List<String> getStartedMsg();
}
