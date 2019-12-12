package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.event.PlayerTeleportDelayEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetDelay extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, String value, HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getEntities();
        if(event instanceof PlayerTeleportDelayEvent) {
            final PlayerTeleportDelayEvent e = (PlayerTeleportDelayEvent) event;
            e.setDelay(evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
