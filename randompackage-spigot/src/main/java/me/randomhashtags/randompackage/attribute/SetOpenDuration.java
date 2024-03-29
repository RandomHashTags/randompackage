package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.dev.dungeons.DungeonPortalOpenEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class SetOpenDuration extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending, @NotNull String value, @NotNull HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getEntities();
        if(event instanceof DungeonPortalOpenEvent) {
            final DungeonPortalOpenEvent e = (DungeonPortalOpenEvent) event;
            e.setTicksOpen((int) evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
