package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class Heal extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, HashMap<String, String> valueReplacements) {
        final HashMap<String, Entity> entities = pending.getEntities();
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof LivingEntity) {
                String value = recipientValues.get(e);
                if(value != null) {
                    final LivingEntity l = (LivingEntity) e;
                    value = replaceValue(entities, value.replace("hp", Double.toString(l.getHealth())), valueReplacements);
                    final double total = evaluate(value);
                    l.setHealth(Math.min(l.getMaxHealth(), total+l.getHealth()));
                }
            }
        }
    }
}
