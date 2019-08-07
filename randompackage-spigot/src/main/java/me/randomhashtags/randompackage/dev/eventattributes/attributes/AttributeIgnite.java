package me.randomhashtags.randompackage.dev.eventattributes.attributes;

import me.randomhashtags.randompackage.dev.eventattributes.AbstractEventAttribute;
import org.bukkit.entity.Entity;

public class AttributeIgnite extends AbstractEventAttribute {
    public String getIdentifier() { return "IGNITE"; }
    public void call(Entity recipient, Object value) {
        if(recipient != null && value != null) {
            final int ticks = (int) value;
            recipient.setFireTicks(ticks);
        }
    }
}
