package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.event.DepleteRarityGemEvent;
import me.randomhashtags.randompackage.event.PlayerTeleportDelayEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class SetDepleteAmount extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending, @NotNull String value, @NotNull HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getEntities();
        if(event instanceof DepleteRarityGemEvent) {
            final DepleteRarityGemEvent e = (DepleteRarityGemEvent) event;
            e.setDepleteAmount((int) evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
