package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class AbstractEventCondition implements EventCondition {
    @NotNull
    @Override
    public String getIdentifier() {
        final String[] className = getClass().getName().split("\\.");
        return className[className.length-1].toUpperCase();
    }
    public void load() {
        register(Feature.EVENT_CONDITION, this);
    }
    public void unload() {}

    public boolean check(@NotNull String value) { return true; }
    public boolean check(@NotNull Event event) { return true; }
    public boolean check(@NotNull Event event, @NotNull Entity entity) { return true; }
    public boolean check(@NotNull Event event, @NotNull String value) { return true; }
    public boolean check(@NotNull Entity entity, @NotNull String value) { return true; }
    public boolean check(@NotNull String entity, @NotNull HashMap<String, Entity> entities, @NotNull String value) { return true; }
}
