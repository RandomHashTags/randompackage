package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addons.utils.Identifiable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;

import java.util.HashMap;

public interface EventAttribute extends Cancellable, Identifiable {
    void load();
    void execute(Object value);
    void execute(HashMap<Entity, Object> recipientValues);
}
