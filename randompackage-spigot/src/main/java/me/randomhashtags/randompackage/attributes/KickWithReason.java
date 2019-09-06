package me.randomhashtags.randompackage.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashMap;

public class KickWithReason extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                ((Player) e).kickPlayer(recipientValues.get(e));
            }
        }
    }
}
