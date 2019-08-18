package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.addons.utils.Rewardable;
import org.bukkit.Location;

import java.util.List;

public interface Stronghold extends Itemable, Rewardable {
    int getSlot();
    Location getLocation();
    List<String> getNoLongerControllingMsg();
    List<String> getTakenControlMsg();
}
