package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import org.bukkit.event.Event;

public interface EventAttributeListener extends Identifiable {
    void called(Event event);
}
