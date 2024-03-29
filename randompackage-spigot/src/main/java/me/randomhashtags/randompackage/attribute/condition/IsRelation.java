package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public final class IsRelation extends AbstractEventCondition {
    @Override
    public boolean check(@NotNull String entity, @NotNull HashMap<String, Entity> entities, @NotNull String value) {
        final String[] values = value.split(":");
        value = values[1];
        final Entity entity1 = entities.get(entity), entity2 = entities.get(values[0]);
        if(entity1 instanceof Player && entity2 instanceof Player) {
            final UUID e1 = entity1.getUniqueId(), e2 = entity2.getUniqueId();
            final RegionalAPI regions = RegionalAPI.INSTANCE;
            switch (value.toUpperCase()) {
                case "ALLY": return regions.getAllies(e1).contains(e2);
                case "ENEMY": return regions.getEnemies(e1).contains(e2);
                case "MEMBER": return regions.getAssociates(e1).contains(e2);
                case "TRUCE": return regions.getTruces(e1).contains(e2);
                default: return false;
            }
        }
        return false;
    }
}

