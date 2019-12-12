package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class SendMessage extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            final String v = recipientValues.get(e);
            if(v != null) {
                if(v.contains("\\n")) {
                    for(String s : v.split("\\n")) {
                        e.sendMessage(colorize(s));
                    }
                } else {
                    e.sendMessage(colorize(v));
                }
            }
        }
    }
}
