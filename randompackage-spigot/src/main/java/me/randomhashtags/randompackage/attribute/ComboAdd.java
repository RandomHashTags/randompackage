package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public final class ComboAdd extends AbstractEventAttribute implements Combo {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            final UUID u = e.getUniqueId();
            final String[] values = recipientValues.get(e).split(":");
            final String identifier = values[0];
            COMBOS.putIfAbsent(u, new HashMap<>());
            final boolean exists = COMBOS.get(u).containsKey(identifier);
            final HashMap<String, Double> combo = COMBOS.get(u);
            combo.put(identifier, exists ? combo.get(identifier) + evaluate(values[1]) : 1.00);
        }
    }
}
