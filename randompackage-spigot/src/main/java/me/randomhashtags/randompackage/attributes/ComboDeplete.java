package me.randomhashtags.randompackage.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.UUID;

public class ComboDeplete extends AbstractEventAttribute implements Combo {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final UUID u = e.getUniqueId();
            if(combos.containsKey(u)) {
                final HashMap<String, Double> combo = combos.get(u);
                final String[] values = recipientValues.get(e).split(":");
                final String identifier = values[0];
                if(combo.containsKey(identifier)) {
                    combo.put(identifier, combo.get(identifier)-evaluate(values[1]));
                }
            }
        }
    }
}
