package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Attributable;
import me.randomhashtags.randompackage.addons.utils.Rewardable;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.LinkedHashMap;
import java.util.List;

public interface DungeonBoss extends Attributable, Rewardable {
    LinkedHashMap<Integer, List<String>> getStageSettings();
    void onDamage(EntityDamageEvent event);
}
