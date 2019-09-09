package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.addon.EventAttribute;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public abstract class AbstractEventAttribute extends RPStorage implements EventAttribute {
    private boolean cancelled;
    public String getIdentifier() {
        final String[] n = getClass().getName().split("\\.");
        return n[n.length-1].toUpperCase();
    }
    public void load() { addEventAttribute(this); }
    public void unload() {}

    protected String replaceValue(String value, HashMap<String, String> valueReplacements) {
        String string = value;
        if(valueReplacements != null) {
            for(String s : valueReplacements.keySet()) {
                string = string.replace(s, valueReplacements.get(s));
            }
        }
        return string;
    }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public void execute(Event event) {}
    public void execute(Event event, String value) {}
    public void execute(Event event, String value, HashMap<String, String> valueReplacements) {}
    public void execute(String value) {}
    public void execute(Entity entity1, Entity entity2, String value) {}
    public void execute(Event event, HashMap<Entity, String> recipientValues) {}
    public void execute(Event event, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {}
    public void executeAt(HashMap<Location, String> locations) {}
    public void executeData(HashMap<RPPlayer, String> recipientValues) {}
}
