package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.event.mob.MobStackDepleteEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public final class DepleteStackSize extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending, @NotNull String value) {
        final Event event = pending.getEvent();
        if(event instanceof MobStackDepleteEvent) {
            final MobStackDepleteEvent depleteEvent = (MobStackDepleteEvent) event;
            depleteEvent.amount = (int) evaluate(value);
        }
    }
}
