package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.event.PlayerTeleportDelayEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetDelay extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<String, Entity> entities, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof PlayerTeleportDelayEvent) {
            final PlayerTeleportDelayEvent e = (PlayerTeleportDelayEvent) event;
            e.setDelay(evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
