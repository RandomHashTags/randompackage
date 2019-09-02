package me.randomhashtags.randompackage.dev;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addons.utils.Identifiable;
import me.randomhashtags.randompackage.addons.utils.Mathable;
import me.randomhashtags.randompackage.utils.RPPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.HashMap;

public interface EventAttribute extends Cancellable, Identifiable, Mathable {
    void load();
    void execute(@NotNull String value);
    void execute(@NotNull Player player, @NotNull Entity entity, @NotNull String value);
    void execute(@NotNull HashMap<Entity, String> recipientValues);
    void executeAt(@NotNull HashMap<Location, String> locations);
    void executeData(@NotNull HashMap<RPPlayer, String> recipientValues);
}
