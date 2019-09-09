package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.util.Rewardable;

import java.util.List;

public interface PlayerRank extends Rewardable {
    String getName();
    List<String> getAddedPermissions();
}
