package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addons.utils.Identifiable;
import me.randomhashtags.randompackage.addons.utils.Mathable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.HashMap;

public interface EventAttribute extends Cancellable, Identifiable, Mathable {
    void load();
    void execute(String value);
    void execute(Player player, String value);
    void execute(Player player, Entity entity, String value);
    void executeAt(HashMap<Location, String> locations);
    void execute(HashMap<Entity, String> recipientValues);
}
