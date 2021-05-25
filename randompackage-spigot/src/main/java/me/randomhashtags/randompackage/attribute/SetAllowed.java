package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public final class SetAllowed extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, String value) {
        final Event event = pending.getEvent();
        if(event instanceof PlayerCommandPreprocessEvent) {
            ((PlayerCommandPreprocessEvent) event).setCancelled(Boolean.parseBoolean(value));
        }
    }
}
