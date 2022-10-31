package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Mathable;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.data.RPPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface EventAttribute extends Cancellable, Identifiable, Mathable {
    void load();
    void unload();
    void execute(@NotNull Entity entity1, @NotNull Entity entity2, @NotNull String value);

    void execute(@NotNull PendingEventAttribute pending);
    void execute(@NotNull PendingEventAttribute pending, @NotNull HashMap<String, String> valueReplacements);
    void execute(@NotNull PendingEventAttribute pending, @NotNull String value);
    void execute(@NotNull PendingEventAttribute pending, @NotNull String value, @NotNull HashMap<String, String> valueReplacements);

    void executeAt(@NotNull HashMap<Location, String> locations);
    void executeData(@NotNull HashMap<RPPlayer, String> recipientValues, @NotNull HashMap<String, String> valueReplacements);
    void executeData(@NotNull HashMap<String, Entity> entities, @NotNull HashMap<RPPlayer, String> recipientValues, @NotNull HashMap<String, String> valueReplacements);
}
