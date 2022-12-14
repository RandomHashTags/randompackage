package me.randomhashtags.randompackage.dev.dungeons;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Rewardable;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.LinkedHashMap;
import java.util.List;

public interface DungeonBoss extends Attributable, Rewardable {
    LinkedHashMap<Integer, List<String>> getStageSettings();
    void onDamage(EntityDamageEvent event);
}
