package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public interface EventAttributeListener extends Identifiable {
    void called(@NotNull Event event);
}
