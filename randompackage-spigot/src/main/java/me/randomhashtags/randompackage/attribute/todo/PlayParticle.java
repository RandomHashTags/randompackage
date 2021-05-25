package me.randomhashtags.randompackage.attribute.todo;

import me.randomhashtags.randompackage.attribute.AbstractEventAttribute;
import me.randomhashtags.randompackage.universal.UParticle;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;

public final class PlayParticle extends AbstractEventAttribute {
    // TODO: finish this attribute
    @Override
    public void executeAt(HashMap<Location, String> locations) {
        for(Location l : locations.keySet()) {
            final World w = l.getWorld();
            final String[] values = locations.get(l).split(":");
            final Object particle = UParticle.matchParticle(values[0].toUpperCase()).getParticle();
            final int amount = Integer.parseInt(values[1]);
            if(EIGHT) {
                w.playEffect(l, (org.bukkit.Effect) particle, amount);
            } else {
                w.spawnParticle((org.bukkit.Particle) particle, l, amount);
            }
        }
    }
}
