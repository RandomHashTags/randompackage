package me.randomhashtags.randompackage.attribute;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SetAllowed extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        if(event instanceof PlayerCommandPreprocessEvent) {
            ((PlayerCommandPreprocessEvent) event).setCancelled(Boolean.parseBoolean(value));
        }
    }
}
