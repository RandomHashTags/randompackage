package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AttributeSetAir extends AbstractEventAttribute {
    public String getIdentifier() { return "SETAIR"; }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final String s = recipientValues.get(e);
            if(s != null && e instanceof Player) {
                final Player player = (Player) e;
                final int air = player.getRemainingAir(), max = player.getMaximumAir(), total = (int) evaluate(s.replace("air", Integer.toString(air)));
                player.setRemainingAir(Math.min(total, max));
            }
        }
    }
}
