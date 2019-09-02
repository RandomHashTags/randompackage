package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.Server;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class AttributeExecuteCommand extends AbstractEventAttribute {
    public String getIdentifier() { return "EXECUTECOMMAND"; }
    @Override
    public void execute(String value) {
        console.getServer().dispatchCommand(console, value);
    }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        final Server s = console.getServer();
        for(Entity e : recipientValues.keySet()) {
            final String cmd = recipientValues.get(e);
            if(cmd != null) {
                s.dispatchCommand(e, cmd.replace("%entity%", e.getName()));
            }
        }
    }
}
