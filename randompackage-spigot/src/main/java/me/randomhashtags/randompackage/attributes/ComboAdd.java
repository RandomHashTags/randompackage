package me.randomhashtags.randompackage.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.UUID;

public class ComboAdd extends AbstractEventAttribute implements Combo {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final UUID u = e.getUniqueId();
            final String[] values = recipientValues.get(e).split(":");
            final String identifier = values[0];
            final boolean e1 = combos.containsKey(u), exists = e1 && combos.get(u).containsKey(identifier);
            if(!e1) combos.put(u, new HashMap<>());
            final HashMap<String, Double> combo = combos.get(u);
            combo.put(identifier, exists ? combo.get(identifier)+evaluate(values[1]) : 1.00);
        }
    }
}
