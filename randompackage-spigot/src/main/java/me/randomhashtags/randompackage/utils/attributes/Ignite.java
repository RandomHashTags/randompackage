package me.randomhashtags.randompackage.utils.attributes;

import me.randomhashtags.randompackage.utils.addons.AbstractEventAttribute;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class Ignite extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            e.setFireTicks((int) evaluate(recipientValues.get(e)));
        }
    }
}
