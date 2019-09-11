package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.UUID;

public class RemoveFromList extends AbstractEventAttribute implements Listable {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final UUID u = e.getUniqueId();
            if(list.containsKey(u)) {
                list.get(u).remove(recipientValues.get(e));
            }
        }
    }
}
