package me.randomhashtags.randompackage.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetWalkSpeed extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                ((Player) e).setWalkSpeed((float) evaluate(recipientValues.get(e)));
            }
        }
    }
}
