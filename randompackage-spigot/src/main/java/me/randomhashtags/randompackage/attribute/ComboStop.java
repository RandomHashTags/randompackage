package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class ComboStop extends AbstractEventAttribute implements Combo {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity entity : recipientValues.keySet()) {
            if(entity != null) {
                COMBOS.remove(entity.getUniqueId());
            }
        }
    }
}
