package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.objects.CustomBossAttack;
import me.randomhashtags.randompackage.addons.objects.CustomMinion;
import me.randomhashtags.randompackage.addons.utils.Attributable;
import me.randomhashtags.randompackage.addons.utils.Rewardable;
import me.randomhashtags.randompackage.addons.utils.Scoreboardable;
import me.randomhashtags.randompackage.addons.utils.Spawnable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public interface CustomBoss extends Attributable, Rewardable, Scoreboardable, Spawnable {
    String getType();
    String getName();
    ItemStack getSpawnItem();
    List<CustomBossAttack> getAttacks();
    HashMap<Integer, List<String>> getMessages();
    int getMessageRadius();
    int getMaxMinions();
    CustomMinion getMinion();
}
