package me.randomhashtags.randompackage.events.eventAttributes;

import me.randomhashtags.randompackage.events.AbstractEvent;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.List;

public class ExecuteAttributesEvent extends AbstractEvent {
    public final Event event;
    public final List<String> attributes;
    public final HashMap<String, String> attributeReplacements;
    public ExecuteAttributesEvent(Event event, List<String> attributes, HashMap<String, String> attributeReplacements) {
        this.event = event;
        this.attributes = attributes;
        this.attributeReplacements = attributeReplacements;
    }
}
