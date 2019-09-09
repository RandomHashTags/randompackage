package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetHunger extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final Player player = e instanceof Player ? (Player) e : null;
            if(player != null) {
                final int lvl = player.getFoodLevel();
                player.setFoodLevel((int) evaluate(recipientValues.get(e).replace("hunger", Integer.toString(lvl))));
            }
        }
    }
}
