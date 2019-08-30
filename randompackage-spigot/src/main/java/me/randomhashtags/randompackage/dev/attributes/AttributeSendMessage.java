package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class AttributeSendMessage extends AbstractEventAttribute {
    public String getIdentifier() { return "SENDMESSAGE"; }
    public void execute(Object value) {}
    public void execute(HashMap<Entity, Object> recipientValues) {
        if(recipientValues != null && !recipientValues.isEmpty()) {
            for(Entity e : recipientValues.keySet()) {
                final String v = (String) recipientValues.get(e);
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
