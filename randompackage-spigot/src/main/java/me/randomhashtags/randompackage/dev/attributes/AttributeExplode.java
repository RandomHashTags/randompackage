package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.Location;

import java.util.HashMap;

public class AttributeExplode extends AbstractEventAttribute {
    public String getIdentifier() { return "EXPLODE"; }
    @Override
    public void executeAt(HashMap<Location, String> locations) {
        for(Location l : locations.keySet()) {
            final String v = locations.get(l);
            if(v != null) {
                final String[] s = v.split(":");
                final int size = s.length;
                l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), (float) evaluate(s[1]), size >= 3 && Boolean.parseBoolean(s[2]), size >= 4 && Boolean.parseBoolean(s[3]));
            }
        }
    }
}
