package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.event.kit.KitPreClaimEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class SetLevelupChance extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending, @NotNull String value, @NotNull HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getEntities();
        if(event instanceof KitPreClaimEvent) {
            final KitPreClaimEvent ev = (KitPreClaimEvent) event;
            ev.setLevelupChance((int) evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
