package me.randomhashtags.randompackage.attributes;

import org.bukkit.entity.Entity;

import java.util.HashMap;

public class ComboStop extends AbstractEventAttribute implements Combo {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            combos.remove(e.getUniqueId());
        }
    }
}
