package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Rewardable;

import java.util.Random;

public interface Lootbag extends Itemable, Rewardable {
    String getRewardSize();
    default int getRandomRewardSize(Random random) {
        final String[] a = getRewardSize().split("-");
        final int min = Integer.parseInt(a[0]);
        return a.length == 1 ? min : min+random.nextInt(Integer.parseInt(a[1])-min+1);
    }
}
