package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AttributeStealXp extends AbstractEventAttribute {
    public String getIdentifier() { return "STEALXP"; }
    @Override
    public void execute(Player player, Entity entity, String value) {
        if(player != null && entity instanceof Player && value != null) {
            final Player victim = (Player) entity;
            final int t = getTotalExperience(victim), v = Integer.parseInt(value), i = Math.min(t, v);
            setTotalExperience(victim, t-i);
            setTotalExperience(player, getTotalExperience(player)+i);
        }
    }
}
