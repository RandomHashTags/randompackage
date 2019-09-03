package me.randomhashtags.randompackage.attributes;

import me.randomhashtags.randompackage.utils.addons.AbstractEventAttribute;
import org.bukkit.Server;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class ExecuteCommand extends AbstractEventAttribute {
    @Override
    public void execute(String value) {
        console.getServer().dispatchCommand(console, value);
    }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final Server s = console.getServer();
            s.dispatchCommand(e, recipientValues.get(e).replace("%entity%", e.getName()));
        }
    }
}
