package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import org.bukkit.event.Event;

public interface EventAttributeListener extends Identifiable {
    void called(@NotNull Event event);
}
