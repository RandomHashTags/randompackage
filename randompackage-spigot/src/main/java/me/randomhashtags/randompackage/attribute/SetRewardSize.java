package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.event.lootbag.LootbagClaimEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class SetRewardSize extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending, @NotNull String value, @NotNull HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getEntities();
        if(event instanceof LootbagClaimEvent) {
            final LootbagClaimEvent e = (LootbagClaimEvent) event;
            e.setRewardSize((int) evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
