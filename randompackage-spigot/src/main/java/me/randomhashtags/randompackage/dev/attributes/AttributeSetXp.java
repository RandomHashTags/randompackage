package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AttributeSetXp extends AbstractEventAttribute {
    public String getIdentifier() { return "SETXP"; }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final String v = recipientValues.get(e);
            if(v != null && e instanceof Player) {
                final Player player = (Player) e;
                setTotalExperience(player, (int) evaluate(v));
            }
        }
    }
}
