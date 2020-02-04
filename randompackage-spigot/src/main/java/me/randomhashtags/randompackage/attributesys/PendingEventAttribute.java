package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.attribute.EventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class PendingEventAttribute {
    private Event event;
    private EventAttribute attribute;
    private HashMap<String, Entity> entities, keyEntities, valueEntities;
    private HashMap<Entity, String> recipientValues;
    private String attributeString;
    public PendingEventAttribute(Event event, EventAttribute attribute, HashMap<String, Entity> entities, HashMap<String, Entity> keyEntities, HashMap<String, Entity> valueEntities, HashMap<Entity, String> recipientValues, String attributeString) {
        this.event = event;
        this.attribute = attribute;
        this.entities = entities;
        this.keyEntities = keyEntities;
        this.valueEntities = valueEntities;
        this.recipientValues = recipientValues;
        this.attributeString = attributeString;
    }

    public Event getEvent() {
        return event;
    }
    public EventAttribute getEventAttribute() {
        return attribute;
    }
    public HashMap<String, Entity> getEntities() {
        return entities;
    }
    public HashMap<String, Entity> getKeyEntities() {
        return keyEntities;
    }
    public HashMap<String, Entity> getValueEntities() {
        return valueEntities;
    }
    public HashMap<Entity, String> getRecipientValues() {
        return recipientValues;
    }
    public String getAttributeString() {
        return attributeString;
    }
}
