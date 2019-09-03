package me.randomhashtags.randompackage.utils.attributes;

import me.randomhashtags.randompackage.utils.addons.AbstractEventAttribute;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class Smite extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final World w = e.getWorld();
            final Location l = e.getLocation();
            for(int i = 1; i <= (int) evaluate(recipientValues.get(e)); i++) {
                w.strikeLightning(l);
            }
        }
    }
}
