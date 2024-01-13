package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class SendMessage extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity entity : recipientValues.keySet()) {
            if(entity != null) {
                String targetMessage = recipientValues.get(entity);
                if(targetMessage != null) {
                    targetMessage = colorize(targetMessage);
                    if(targetMessage.contains("\\n")) {
                        for(String string : targetMessage.split("\\n")) {
                            entity.sendMessage(string);
                        }
                    } else {
                        entity.sendMessage(targetMessage);
                    }
                }
            }
        }
    }
}
