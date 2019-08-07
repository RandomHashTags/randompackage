package me.randomhashtags.randompackage.dev.eventattributes.attributes;

import me.randomhashtags.randompackage.dev.eventattributes.AbstractEventAttribute;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

public class AttributeSendMessage extends AbstractEventAttribute {
    public String getIdentifier() { return "sendmessage"; }
    public void call(Entity recipient, Object value) {
        if(recipient != null && value != null) {
            recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', (String) value));
        }
    }
}
