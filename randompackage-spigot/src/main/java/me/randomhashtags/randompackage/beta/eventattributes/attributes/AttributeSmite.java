package me.randomhashtags.randompackage.beta.eventattributes.attributes;

import me.randomhashtags.randompackage.beta.eventattributes.AbstractEventAttribute;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class AttributeSmite extends AbstractEventAttribute {
    public String getIdentifier() { return "SMITE"; }
    public void call(HashMap<Entity, Object> recipientValues) {
        if(recipientValues != null && !recipientValues.isEmpty()) {
            for(Entity e : recipientValues.keySet()) {
                final World w = e.getWorld();
                final Location l = e.getLocation();
                for(int i = 1; i <= (int) recipientValues.get(e); i++) {
                    w.strikeLightning(l);
                }
            }
        }
    }
}
