package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class SetAir extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final Player player = (Player) e;
                final int air = player.getRemainingAir(), max = player.getMaximumAir(), total = (int) evaluate(recipientValues.get(e).replace("air", Integer.toString(air)));
                player.setRemainingAir(Math.min(total, max));
            }
        }
    }
}
