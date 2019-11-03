package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.dev.dungeons.DungeonPortalOpenEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetOpenDuration extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<String, Entity> entities, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof DungeonPortalOpenEvent) {
            final DungeonPortalOpenEvent e = (DungeonPortalOpenEvent) event;
            e.setTicksOpen((int) evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
