package me.randomhashtags.randompackage.attribute;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class Smite extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<String, Entity> entities, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {
        for(Entity e : recipientValues.keySet()) {
            final String v = recipientValues.get(e);
            if(v != null) {
                final String[] values = v.split(":");
                final boolean at = values.length >= 2;
                final String value = replaceValue(entities, at ? values[1] : values[0], valueReplacements);
                final World w = e.getWorld();
                final Location l = at ? toLocation(replaceValue(entities, values[0], valueReplacements)) : e.getLocation();
                for(int i = 1; i <= (int) evaluate(value); i++) {
                    w.strikeLightning(l);
                }
            }
        }
    }
}
