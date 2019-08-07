package me.randomhashtags.randompackage.dev.eventattributes.attributes;

import me.randomhashtags.randompackage.dev.eventattributes.AbstractEventAttribute;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class AttributeSmite extends AbstractEventAttribute {
    public String getIdentifier() { return "SMITE"; }
    public void call(Entity recipient, Object value) {
        if(recipient != null && value != null) {
            final World w = recipient.getWorld();
            final Location l = recipient.getLocation();
            for(int i = 1; i <= (int) value; i++) {
                w.strikeLightning(l);
            }
        }
    }
}
