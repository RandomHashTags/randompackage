package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public final class AddToList extends AbstractEventAttribute implements Listable {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity entity : recipientValues.keySet()) {
            if(entity != null) {
                final UUID uuid = entity.getUniqueId();
                LIST.putIfAbsent(uuid, new HashSet<>());
                LIST.get(uuid).add(recipientValues.get(entity));
            }
        }
    }
}
