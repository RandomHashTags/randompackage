package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.EventReplacer;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.data.RPPlayer;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class AbstractEventAttribute implements EventAttribute, EventReplacer, RPStorage {
    private boolean cancelled;

    @Override
    public @NotNull String getIdentifier() {
        final String[] className = getClass().getName().split("\\.");
        return className[className.length-1].toUpperCase();
    }
    public void load() {
        register(Feature.EVENT_ATTRIBUTE, this);
    }
    public void unload() {}

    public boolean isCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void execute(@NotNull PendingEventAttribute pending) {}
    public void execute(@NotNull PendingEventAttribute pending, @NotNull HashMap<String, String> valueReplacements) {}
    public void execute(@NotNull PendingEventAttribute pending, @NotNull String value) {}
    public void execute(@NotNull PendingEventAttribute pending, @NotNull String value, @NotNull HashMap<String, String> valueReplacements) {}

    public void execute(String value) {}
    public void execute(@NotNull Entity entity1, @NotNull Entity entity2, @NotNull String value) {}
    public void executeAt(@NotNull HashMap<Location, String> locations) {}
    public void executeData(@NotNull HashMap<RPPlayer, String> recipientValues, @NotNull HashMap<String, String> valueReplacements) {}
    public void executeData(@NotNull HashMap<String, Entity> entities, @NotNull HashMap<RPPlayer, String> recipientValues, @NotNull HashMap<String, String> valueReplacements) {}
}
