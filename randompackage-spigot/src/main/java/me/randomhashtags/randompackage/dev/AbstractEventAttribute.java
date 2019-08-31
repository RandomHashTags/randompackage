package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.utils.RPStorage;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class AbstractEventAttribute extends RPStorage implements EventAttribute {
    private boolean cancelled;
    public void load() { addEventAttribute(getIdentifier(), this); }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public void execute(String value) {}
    public void execute(Player player, String value) {}
    public void execute(Player player, Entity entity, String value) {}
    public void executeAt(HashMap<Location, String> locations) {}
    public void execute(HashMap<Entity, String> recipientValues) {}
}
