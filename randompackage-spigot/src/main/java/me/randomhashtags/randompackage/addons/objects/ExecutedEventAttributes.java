package me.randomhashtags.randompackage.addons.objects;

import org.bukkit.event.Event;

import java.util.LinkedHashMap;

public class ExecutedEventAttributes {
    public final Event event;
    public final LinkedHashMap<String, String> executedAttributes;
    public ExecutedEventAttributes(Event event, LinkedHashMap<String, String> executedAttributes) {
        this.event = event;
        this.executedAttributes = executedAttributes;
    }
}
