package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class IsRelation extends AbstractEventCondition {
    @Override
    public boolean check(String entity, HashMap<String, Entity> entities, String value) {
        final String[] values = value.split(":");
        value = values[1];
        final Entity entity1 = entities.get(entity), entity2 = entities.get(values[0]);
        System.out.println("IsRelation;entities=" + entities.toString());
        System.out.println("IsRelation;value=" + value + ";values[0]=" + values[0]);
        System.out.println("IsRelation;entity1=" + entity1 + ";entity2=" + entity2);
        if(entity1 instanceof Player && entity2 instanceof Player) {
            final UUID e1 = entity1.getUniqueId(), e2 = entity2.getUniqueId();
            System.out.println("IsRelation;" + e1.toString() + " -> " + e2.toString());
            switch (value.toUpperCase()) {
                case "ALLY": return getAllies(e1).contains(e2);
                case "ENEMY": return getEnemies(e1).contains(e2);
                case "MEMBER": return getAssociates(e1).contains(e2);
                case "TRUCE": return getTruces(e1).contains(e2);
                default: return false;
            }
        }
        return false;
    }
}

