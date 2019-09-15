package me.randomhashtags.randompackage.addon;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Mathable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

public interface EventCondition extends Identifiable, Mathable {
    void load();
    void unload();
    boolean check(@NotNull String value);
    boolean check(@NotNull Event event);
    boolean check(@NotNull Event event, @NotNull Entity entity);
    boolean check(@NotNull Event event, @NotNull String value);
    boolean check(@NotNull Entity entity, @NotNull String value);
}
