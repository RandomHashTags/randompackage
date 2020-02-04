package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.UUID;

public class ComboDeplete extends AbstractEventAttribute implements Combo {
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            final UUID u = e.getUniqueId();
            if(COMBOS.containsKey(u)) {
                final HashMap<String, Double> combo = COMBOS.get(u);
                final String[] values = recipientValues.get(e).split(":");
                final String identifier = values[0];
                if(combo.containsKey(identifier)) {
                    combo.put(identifier, combo.get(identifier)-evaluate(values[1]));
                }
            }
        }
    }
}
