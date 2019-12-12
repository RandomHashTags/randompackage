package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetHunger extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            final Player player = e instanceof Player ? (Player) e : null;
            if(player != null) {
                final int lvl = player.getFoodLevel();
                player.setFoodLevel((int) evaluate(recipientValues.get(e).replace("hunger", Integer.toString(lvl))));
            }
        }
    }
}
