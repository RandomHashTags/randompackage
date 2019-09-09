package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import me.randomhashtags.randompackage.attribute.Combo;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class HasCombo extends AbstractEventCondition implements Combo {
    @Override
    public boolean check(Entity entity, String value) {
        final UUID u = entity.getUniqueId();
        return combos.containsKey(u) && combos.get(u).containsKey(value);
    }
}
