package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.Server;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class AttributeExecuteCommand extends AbstractEventAttribute {
    public String getIdentifier() { return "EXECUTECOMMAND"; }
    public void execute(Object value) {
        if(value != null) {
            console.getServer().dispatchCommand(console, (String) value);
        }
    }
    public void execute(HashMap<Entity, Object> recipientValues) {
        if(recipientValues != null) {
            final Server s = console.getServer();
            for(Entity e : recipientValues.keySet()) {
                final Object cmd = recipientValues.get(e);
                if(cmd != null) {
                    s.dispatchCommand(e, (String) cmd);
                }
            }
        }
    }
}
