package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class AttributeIgnite extends AbstractEventAttribute {
    public String getIdentifier() { return "IGNITE"; }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final String value = recipientValues.get(e);
            if(value != null) {
                e.setFireTicks((int) evaluate(value));
            }
        }
    }
}
