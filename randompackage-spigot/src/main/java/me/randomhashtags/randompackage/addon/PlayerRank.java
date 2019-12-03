package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.addon.util.Rewardable;

import java.util.List;

public interface PlayerRank extends Nameable, Rewardable {
    List<String> getAddedPermissions();
}
