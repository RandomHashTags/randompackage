package me.randomhashtags.randompackage.dev.eventattributes.attributes;

import me.randomhashtags.randompackage.dev.eventattributes.AbstractEventAttribute;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

public class AttributeSendMessage extends AbstractEventAttribute {
    public String getIdentifier() { return "sendmessage"; }
    public void call(Entity recipient, Object value) {
        if(recipient != null && value != null) {
            final String v = (String) value;
            if(v.contains("\\n")) {
                for(String s : v.split("\\n")) {
                    recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
            } else {
                recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', v));
            }
        }
    }
}
