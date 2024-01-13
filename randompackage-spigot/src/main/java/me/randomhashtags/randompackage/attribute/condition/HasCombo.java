package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import me.randomhashtags.randompackage.attribute.Combo;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class HasCombo extends AbstractEventCondition implements Combo {
    @Override
    public boolean check(@NotNull Entity entity, @NotNull String value) {
        final String[] values = value.split(":");
        final String key = values[0];
        final boolean status = values.length == 1 || Boolean.parseBoolean(values[1]);
        final UUID uuid = entity.getUniqueId();
        return COMBOS.containsKey(uuid) && COMBOS.get(uuid).containsKey(key) == status;
    }
}
