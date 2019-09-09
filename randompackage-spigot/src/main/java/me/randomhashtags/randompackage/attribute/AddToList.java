package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AddToList extends AbstractEventAttribute implements Listable {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final UUID u = e.getUniqueId();
            if(!list.containsKey(u)) list.put(u, new ArrayList<>());
            list.get(u).add(recipientValues.get(e));
        }
    }
}
