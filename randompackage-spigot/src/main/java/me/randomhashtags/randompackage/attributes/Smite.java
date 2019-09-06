package me.randomhashtags.randompackage.attributes;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class Smite extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final World w = e.getWorld();
            final Location l = e.getLocation();
            for(int i = 1; i <= (int) evaluate(recipientValues.get(e)); i++) {
                w.strikeLightning(l);
            }
        }
    }
}
