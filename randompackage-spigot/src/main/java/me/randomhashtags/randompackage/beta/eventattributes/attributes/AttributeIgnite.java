package me.randomhashtags.randompackage.beta.eventattributes.attributes;

import me.randomhashtags.randompackage.beta.eventattributes.AbstractEventAttribute;
import org.bukkit.entity.Entity;

public class AttributeIgnite extends AbstractEventAttribute {
    public String getIdentifier() { return "IGNITE"; }
    public void call(HashMap<Entity, Object> recipientValues) {
        if(recipientValues != null && !recipientValues.isEmpty()) {
            for(Entity e : recipientValues.keySet()) {
                final int ticks = (int) recipientValues.get(e);
                e.setFireTicks(ticks);
            }
        }
    }
}
