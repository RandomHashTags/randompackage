package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class StealXp extends AbstractEventAttribute {
    @Override
    public void execute(Entity entity1, Entity entity2, String value) {
        if(entity1 instanceof Player && entity2 instanceof Player && value != null) {
            final Player player = (Player) entity1, victim = (Player) entity2;
            final int t = getTotalExperience(victim), v = Integer.parseInt(value), i = Math.min(t, v);
            setTotalExperience(victim, t-i);
            setTotalExperience(player, getTotalExperience(player)+i);
        }
    }
}
