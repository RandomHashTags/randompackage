package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class SetCombo extends AbstractEventAttribute implements Combo {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity entity : recipientValues.keySet()) {
            if(entity != null) {
                final UUID uuid = entity.getUniqueId();
                final String[] values = recipientValues.get(entity).split(":");
                final String identifier = values[0];
                if(!COMBOS.containsKey(uuid)) {
                    COMBOS.put(uuid, new HashMap<>());
                }
                final HashMap<String, Double> combo = COMBOS.get(uuid);
                combo.put(identifier, evaluate(values[1]));
            }
        }
    }
}
