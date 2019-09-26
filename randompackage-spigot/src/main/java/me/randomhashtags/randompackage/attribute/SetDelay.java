package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.event.PlayerTeleportDelayEvent;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetDelay extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof PlayerTeleportDelayEvent) {
            final PlayerTeleportDelayEvent e = (PlayerTeleportDelayEvent) event;
            e.setDelay(evaluate(replaceValue(value, valueReplacements)));
        }
    }
}
