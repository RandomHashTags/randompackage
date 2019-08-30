package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AttributeSetHunger extends AbstractEventAttribute {
    public String getIdentifier() { return "SETHUNGER"; }
    public void execute(Object value) {}
    public void execute(HashMap<Entity, Object> recipientValues) {
        if(recipientValues != null) {
            for(Entity e : recipientValues.keySet()) {
                final Player player = e instanceof Player ? (Player) e : null;
                if(player != null) {
                    final Object v = recipientValues.get(e);
                    if(v != null) {
                        player.setFoodLevel((int) v);
                    }
                }
            }
        }
    }
}
