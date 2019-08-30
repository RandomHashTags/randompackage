package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class AttributeSmite extends AbstractEventAttribute {
    public String getIdentifier() { return "SMITE"; }
    public void execute(Object value) {}
    public void execute(HashMap<Entity, Object> recipientValues) {
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
