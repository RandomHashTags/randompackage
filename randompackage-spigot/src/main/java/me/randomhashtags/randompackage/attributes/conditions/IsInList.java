package me.randomhashtags.randompackage.attributes.conditions;

import me.randomhashtags.randompackage.attributes.AbstractEventCondition;
import me.randomhashtags.randompackage.attributes.Listable;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class IsInList extends AbstractEventCondition implements Listable {
    @Override
    public boolean check(Entity entity, String value) {
        final UUID u = entity.getUniqueId();
        return list.containsKey(u) && list.get(u).contains(value);
    }
}
