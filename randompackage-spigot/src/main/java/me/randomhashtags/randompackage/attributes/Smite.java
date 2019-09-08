package me.randomhashtags.randompackage.attributes;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class Smite extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues, HashMap<Entity, HashMap<String, String>> valueReplacements) {
        for(Entity e : recipientValues.keySet()) {
            final HashMap<String, String> replacements = valueReplacements.get(e);
            final String v = recipientValues.get(e);
            if(v != null) {
                final String[] values = v.split(":");
                final boolean at = values.length >= 2;
                final String value = replaceValue(at ? values[1] : values[0], replacements);
                final World w = e.getWorld();
                final Location l = at ? toLocation(replaceValue(values[0], replacements)) : e.getLocation();
                for(int i = 1; i <= (int) evaluate(value); i++) {
                    w.strikeLightning(l);
                }
            }
        }
    }
}
