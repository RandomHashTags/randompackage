package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addons.utils.Scheduleable;
import me.randomhashtags.randompackage.addons.utils.Scoreboardable;
import org.bukkit.Location;

import java.util.List;

public interface BattlefieldEvent extends Scheduleable, Scoreboardable {
    Location getWarpLocation();
    List<String> getAllowedCommands();

    List<String> getStartedMsg();
}
