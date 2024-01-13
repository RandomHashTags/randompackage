package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class SetNoDamageTicks extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof LivingEntity) {
                final LivingEntity l = (LivingEntity) e;
                l.setNoDamageTicks((int) evaluate(recipientValues.get(e).replace("ticks", Integer.toString(l.getNoDamageTicks()))));
            }
        }
    }
}
