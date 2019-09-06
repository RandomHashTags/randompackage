package me.randomhashtags.randompackage.attributes.todo;

import me.randomhashtags.randompackage.attributes.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class ChangeVelocity extends AbstractEventAttribute {
    // TODO: finish this attribute
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
        }
    }
}
