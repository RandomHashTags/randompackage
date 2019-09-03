package me.randomhashtags.randompackage.attributes;

import me.randomhashtags.randompackage.utils.addons.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetHunger extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final Player player = e instanceof Player ? (Player) e : null;
            if(player != null) {
                final int lvl = player.getFoodLevel();
                player.setFoodLevel((int) evaluate(recipientValues.get(e).replace("lvl", Integer.toString(lvl))));
            }
        }
    }
}
