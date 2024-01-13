package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public final class RemoveFromList extends AbstractEventAttribute implements Listable {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity entity : recipientValues.keySet()) {
            if(entity != null) {
                final UUID u = entity.getUniqueId();
                if(LIST.containsKey(u)) {
                    LIST.get(u).remove(recipientValues.get(entity));
                }
            }
        }
    }
}
