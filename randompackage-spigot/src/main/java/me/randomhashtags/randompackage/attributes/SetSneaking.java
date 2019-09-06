package me.randomhashtags.randompackage.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetSneaking extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                ((Player) e).setSneaking(Boolean.parseBoolean(recipientValues.get(e)));
            }
        }
    }
}
