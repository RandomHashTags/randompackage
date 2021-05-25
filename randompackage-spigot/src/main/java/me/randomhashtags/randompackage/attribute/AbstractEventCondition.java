package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public abstract class AbstractEventCondition implements EventCondition {
    @Override
    public String getIdentifier() {
        final String[] className = getClass().getName().split("\\.");
        return className[className.length-1].toUpperCase();
    }
    public void load() {
        register(Feature.EVENT_CONDITION, this);
    }
    public void unload() {}

    public boolean check(String value) { return true; }
    public boolean check(Event event) { return true; }
    public boolean check(Event event, Entity entity) { return true; }
    public boolean check(Event event, String value) { return true; }
    public boolean check(Entity entity, String value) { return true; }
    public boolean check(String entity, HashMap<String, Entity> entities, String value) { return true; }
}
