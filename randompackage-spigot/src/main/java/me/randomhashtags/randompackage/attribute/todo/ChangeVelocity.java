package me.randomhashtags.randompackage.attribute.todo;

import me.randomhashtags.randompackage.attribute.AbstractEventAttribute;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public final class ChangeVelocity extends AbstractEventAttribute {
    // TODO: finish this attribute
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
        }
    }
}
