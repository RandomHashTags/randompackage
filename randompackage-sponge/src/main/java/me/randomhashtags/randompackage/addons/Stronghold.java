package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.spongepowered.api.world.Location;

import java.util.List;

public interface Stronghold extends Itemable {
    int getSlot();
    Location getLocation();
    List<String> getRewards();
    List<String> getNoLongerControllingMsg();
    List<String> getTakenControlMsg();
}
