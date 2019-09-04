package me.randomhashtags.randompackage.addons;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addons.utils.Identifiable;
import me.randomhashtags.randompackage.addons.utils.Mathable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

public interface EventCondition extends Identifiable, Mathable {
    void load();
    boolean check(@NotNull Event event);
    boolean check(@NotNull Event event, @NotNull Entity entity);
    boolean check(@NotNull Event event, @NotNull String value);
    boolean check(@NotNull Entity entity, @NotNull String value);
}
