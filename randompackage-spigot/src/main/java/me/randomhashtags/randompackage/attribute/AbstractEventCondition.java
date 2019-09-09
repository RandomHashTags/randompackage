package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.addon.EventCondition;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

public abstract class AbstractEventCondition extends RPStorage implements EventCondition {
    public String getIdentifier() {
        final String[] n = getClass().getName().split("\\.");
        return n[n.length-1].toUpperCase();
    }
    public void load() { addEventCondition(this); }
    public void unload() {}

    public boolean check(Event event) { return true; }
    public boolean check(Event event, Entity entity) { return true; }
    public boolean check(Event event, String value) { return true; }
    public boolean check(Entity entity, String value) { return true; }
}
