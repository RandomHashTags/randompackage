package me.randomhashtags.randompackage.attribute.todo;

import me.randomhashtags.randompackage.attribute.AbstractEventAttribute;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class ChangeVelocity extends AbstractEventAttribute {
    // TODO: finish this attribute
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
        }
    }
}
