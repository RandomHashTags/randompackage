package me.randomhashtags.randompackage.addons;

import com.sun.istack.internal.NotNull;
import org.bukkit.event.Event;

public interface EventAttributeHandler {
    void handle(@NotNull Event event, @NotNull String value);
}
