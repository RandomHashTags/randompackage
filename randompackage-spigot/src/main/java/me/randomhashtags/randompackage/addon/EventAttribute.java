package me.randomhashtags.randompackage.addon;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Mathable;
import me.randomhashtags.randompackage.util.RPPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.HashMap;

public interface EventAttribute extends Cancellable, Identifiable, Mathable {
    void load();
    void unload();
    void execute(@NotNull Event event);
    void execute(@NotNull Event event, @NotNull String value);
    void execute(@NotNull Event event, @NotNull String value, @NotNull HashMap<String, String> valueReplacements);
    void execute(@NotNull Entity entity1, @NotNull Entity entity2, @NotNull String value);
    void execute(@NotNull Event event, @NotNull HashMap<Entity, String> recipientValues);
    void execute(@NotNull Event event, @NotNull HashMap<Entity, String> recipientValues, @NotNull HashMap<String, String> valueReplacements);
    void executeAt(@NotNull HashMap<Location, String> locations);
    void executeData(@NotNull HashMap<RPPlayer, String> recipientValues);
}
