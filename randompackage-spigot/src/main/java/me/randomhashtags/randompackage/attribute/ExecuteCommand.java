package me.randomhashtags.randompackage.attribute;

import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class ExecuteCommand extends AbstractEventAttribute {
    @Override
    public void execute(String value) {
        CONSOLE.getServer().dispatchCommand(CONSOLE, value);
    }
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final Server s = CONSOLE.getServer();
            s.dispatchCommand(e, recipientValues.get(e).replace("%entity%", e.getName()));
        }
    }
}
