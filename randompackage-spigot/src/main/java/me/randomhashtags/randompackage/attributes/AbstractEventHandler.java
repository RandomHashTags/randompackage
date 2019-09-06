package me.randomhashtags.randompackage.attributes;

import me.randomhashtags.randompackage.addons.EventAttributeHandler;
import me.randomhashtags.randompackage.utils.RPStorage;
import org.bukkit.event.Event;

public abstract class AbstractEventHandler extends RPStorage implements EventAttributeHandler {
    public void handle(Event event, String value) {}
}
