package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.UUID;

public class RemoveFromList extends AbstractEventAttribute implements Listable {
    @Override
    public void execute(PendingEventAttribute pending) {
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
