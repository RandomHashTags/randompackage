package me.randomhashtags.randompackage.dev.eventattributes;

import me.randomhashtags.randompackage.api.events.EventAttributeCallEvent;
import me.randomhashtags.randompackage.utils.RPStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractEventAttribute extends RPStorage implements EventAttribute {
    private static final PluginManager m = Bukkit.getPluginManager();
    private boolean cancelled;
    private HashMap<String, Entity> recipients;

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public void load() { addEventAttribute(getIdentifier(), this); }
    public boolean didPassConditions(List<String> conditions) {
        boolean passed = true;
        final HashMap<String, Entity> entities = new HashMap<>();
        for(String r : recipients.keySet()) entities.put(r.toLowerCase(), recipients.get(r));
        for(String c : conditions) {
            final String condition = c.toLowerCase();
            for(String s : entities.keySet()) {
                final Entity e = entities.get(s);
                if(condition.startsWith(s)) {
                    if(condition.equals(s + "isplayer")) {
                        passed = e instanceof Player;
                    } else if(condition.equals(s + "ismob")) {
                        passed = e instanceof Mob;
                    } else if(condition.equals(s + "iscreature")) {
                        passed = e instanceof Creature;
                    } else if(condition.startsWith(s + "type=")) {
                        passed = e.getType().name().toLowerCase().equals(condition.split("=")[1]);
                    }
                } else if(condition.startsWith("cancelled=")) {
                    passed = isCancelled() == Boolean.parseBoolean(condition.split("=")[1]);
                }
                if(!passed) break;
            }
        }
        return passed;
    }
    public void execute(Event event, HashMap<String, Entity> recipients, List<String> conditions, HashMap<Entity, List<EventAttribute>> attributes, Object value, HashMap<String, String> valueReplacements) {
        this.recipients = recipients;
        if(didPassConditions(conditions)) {
            for(Entity entity : attributes.keySet()) {
                for(EventAttribute a : attributes.get(entity)) {
                    final EventAttributeCallEvent e = new EventAttributeCallEvent(entity, a);
                    m.callEvent(e);
                    if(!e.isCancelled()) {
                        call(entity, value);
                    }
                }
            }
        }
    }
    public abstract void call(Entity recipient, Object value);
}
