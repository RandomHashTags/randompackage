package me.randomhashtags.randompackage.attributes.todo;

import me.randomhashtags.randompackage.attributes.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class GiveEco extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        // TODO: Support more than 1 economy plugin & finish this attribute
    }
}
