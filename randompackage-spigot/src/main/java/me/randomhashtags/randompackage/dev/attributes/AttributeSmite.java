package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class AttributeSmite extends AbstractEventAttribute {
    public String getIdentifier() { return "SMITE"; }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final String v = recipientValues.get(e);
            if(v != null) {
                final World w = e.getWorld();
                final Location l = e.getLocation();
                for(int i = 1; i <= Integer.parseInt(v); i++) {
                    w.strikeLightning(l);
                }
            }
        }
    }
}
