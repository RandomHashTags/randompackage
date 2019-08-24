package me.randomhashtags.randompackage.beta.eventattributes;

import me.randomhashtags.randompackage.addons.utils.Identifiable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.List;

public interface EventAttribute extends Cancellable, Identifiable {
    void load();
    boolean didPassConditions(List<String> conditions);
    void execute(Event event, HashMap<String, Entity> recipients, List<String> conditions, HashMap<Entity, List<EventAttribute>> attributes, Object value, HashMap<String, String> valueReplacements);
}
