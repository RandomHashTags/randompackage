package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AttributeSetHunger extends AbstractEventAttribute {
    public String getIdentifier() { return "SETHUNGER"; }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final Player player = e instanceof Player ? (Player) e : null;
            final String v = recipientValues.get(e);
            if(v != null && player != null) {
                final int lvl = player.getFoodLevel();
                player.setFoodLevel((int) evaluate(v.replace("lvl", Integer.toString(lvl))));
            }
        }
    }
}
