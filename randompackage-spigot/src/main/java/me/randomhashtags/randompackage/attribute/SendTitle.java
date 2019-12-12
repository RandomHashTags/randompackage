package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SendTitle extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final String[] values = recipientValues.get(e).split(":");
                final int l = values.length;
                ((Player) e).sendTitle(values[0], l >= 2 ? values[1] : null);
            }
        }
    }
}
