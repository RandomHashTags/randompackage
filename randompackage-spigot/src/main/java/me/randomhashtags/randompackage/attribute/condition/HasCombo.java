package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import me.randomhashtags.randompackage.attribute.Combo;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class HasCombo extends AbstractEventCondition implements Combo {
    @Override
    public boolean check(Entity entity, String value) {
        final String[] values = value.split(":");
        final String key = values[0];
        final boolean status = values.length == 1 || Boolean.parseBoolean(values[1]);
        final UUID u = entity.getUniqueId();
        return COMBOS.containsKey(u) && COMBOS.get(u).containsKey(key) == status;
    }
}
