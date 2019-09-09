package me.randomhashtags.randompackage.attribute;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SendMessage extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final String v = recipientValues.get(e);
            if(v != null) {
                if(v.contains("\\n")) {
                    for(String s : v.split("\\n")) {
                        e.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                    }
                } else {
                    e.sendMessage(ChatColor.translateAlternateColorCodes('&', v));
                }
            }
        }
    }
}
