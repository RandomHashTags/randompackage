package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.UUID;

public class SetCombo extends AbstractEventAttribute implements Combo {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final UUID u = e.getUniqueId();
            final String[] values = recipientValues.get(e).split(":");
            final String identifier = values[0];
            if(!combos.containsKey(u)) combos.put(u, new HashMap<>());
            final HashMap<String, Double> combo = combos.get(u);
            combo.put(identifier, evaluate(values[1]));
        }
    }
}
