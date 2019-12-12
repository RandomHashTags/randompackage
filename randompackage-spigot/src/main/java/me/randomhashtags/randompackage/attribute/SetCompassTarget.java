package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetCompassTarget extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final String[] values = recipientValues.get(e).split(":");
                ((Player) e).setCompassTarget(new Location(e.getWorld(), evaluate(values[0]), evaluate(values[1]), evaluate(values[2])));
            }
        }
    }
}
