package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.UUID;

public class SetCombo extends AbstractEventAttribute implements Combo {
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
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
