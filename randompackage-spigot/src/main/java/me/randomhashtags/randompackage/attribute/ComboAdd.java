package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.UUID;

public class ComboAdd extends AbstractEventAttribute implements Combo {
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            final UUID u = e.getUniqueId();
            final String[] values = recipientValues.get(e).split(":");
            final String identifier = values[0];
            final boolean e1 = COMBOS.containsKey(u), exists = e1 && COMBOS.get(u).containsKey(identifier);
            if(!e1) COMBOS.put(u, new HashMap<>());
            final HashMap<String, Double> combo = COMBOS.get(u);
            combo.put(identifier, exists ? combo.get(identifier)+evaluate(values[1]) : 1.00);
        }
    }
}
