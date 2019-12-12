package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.Server;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class ExecuteCommand extends AbstractEventAttribute {
    @Override
    public void execute(String value) {
        CONSOLE.getServer().dispatchCommand(CONSOLE, value);
    }
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            final Server s = CONSOLE.getServer();
            s.dispatchCommand(e, recipientValues.get(e).replace("%entity%", e.getName()));
        }
    }
}
