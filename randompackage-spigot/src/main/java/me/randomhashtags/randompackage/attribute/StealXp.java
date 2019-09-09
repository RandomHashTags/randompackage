package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class StealXp extends AbstractEventAttribute {
    @Override
    public void execute(Entity entity1, Entity entity, String value) {
        if(entity1 instanceof Player && entity instanceof Player && value != null) {
            final Player player = (Player) entity1, victim = (Player) entity;
            final int t = getTotalExperience(victim), v = Integer.parseInt(value), i = Math.min(t, v);
            setTotalExperience(victim, t-i);
            setTotalExperience(player, getTotalExperience(player)+i);
        }
    }
}
