package me.randomhashtags.randompackage.attributes.conditions;

import me.randomhashtags.randompackage.attributes.AbstractEventCondition;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class HitBlock extends AbstractEventCondition {
    @Override
    public boolean check(Event event, String value) {
        final PlayerInteractEvent e = event instanceof PlayerInteractEvent ? (PlayerInteractEvent) event : null;
        if(e != null) {
            final Block b = e.getClickedBlock();
            return b != null && b.getType().name().endsWith(value.toUpperCase());
        }
        return false;
    }
}
