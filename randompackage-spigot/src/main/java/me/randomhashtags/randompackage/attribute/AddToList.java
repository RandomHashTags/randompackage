package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AddToList extends AbstractEventAttribute implements Listable {
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            final UUID u = e.getUniqueId();
            if(!list.containsKey(u)) list.put(u, new ArrayList<>());
            list.get(u).add(recipientValues.get(e));
        }
    }
}
