package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.List;

public class newnewEventAttributes extends RPFeature {
    private static newnewEventAttributes instance;
    public static newnewEventAttributes getEventAttributes() {
        if(instance == null) instance = new newnewEventAttributes();
        return instance;
    }
    public void load() {
    }
    public void unload() {
    }


    public HashMap<String, Entity> getEntities(Object...values) {
        final HashMap<String, Entity> e = new HashMap<>();
        for(int i = 0; i < values.length; i++) {
            if(i%2 == 1) {
                e.put((String) values[i-1], (Entity) values[i]);
            }
        }
        return e;
    }

    private void tryGeneric(Event event, HashMap<String, Entity> entities, List<String> attributes) {
        if(event != null && attributes != null) {
            final String first = attributes.get(0).split(";")[0].toLowerCase();
            if(first.equals(event.getEventName().split("Event")[0])) {

            }
        }
    }

    public void trigger(EntityDeathEvent event, List<String> attributes) {
        final LivingEntity e = event.getEntity();
        final Player k = e.getKiller();
        if(k != null) {
            tryGeneric(event, getEntities("Victim", e, "Killer", k), attributes);
        }
    }
}
