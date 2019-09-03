package me.randomhashtags.randompackage.utils.attributes;

import me.randomhashtags.randompackage.utils.addons.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetXp extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final Player player = (Player) e;
                setTotalExperience(player, (int) evaluate(recipientValues.get(e)));
            }
        }
    }
}
