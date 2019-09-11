package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class Remove extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(Boolean.parseBoolean(recipientValues.get(e))) {
                e.remove();
            }
        }
    }
}
