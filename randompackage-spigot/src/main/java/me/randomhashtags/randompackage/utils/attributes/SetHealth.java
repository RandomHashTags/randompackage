package me.randomhashtags.randompackage.utils.attributes;

import me.randomhashtags.randompackage.utils.addons.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class SetHealth extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof LivingEntity) {
                final LivingEntity l = (LivingEntity) e;
                final double hp = l.getHealth(), max = l.getMaxHealth(), total = evaluate(recipientValues.get(e).replace("hp", Double.toString(hp)));
                l.setHealth(total < 0.00 ? 0.00 : Math.min(total, max));
            }
        }
    }
}
