package me.randomhashtags.randompackage.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetSprinting extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                ((Player) e).setSprinting(Boolean.parseBoolean(recipientValues.get(e)));
            }
        }
    }
}
