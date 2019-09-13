package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.addon.util.Rewardable;

import java.util.List;

public interface PlayerRank extends Nameable, Rewardable {
    List<String> getAddedPermissions();
}
