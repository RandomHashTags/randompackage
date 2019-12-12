package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class BreakHitBlock extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending) {
        final Event event = pending.getEvent();
        if(event instanceof PlayerInteractEvent) {
            final PlayerInteractEvent e = (PlayerInteractEvent) event;
            final Block b = e.getClickedBlock();
            if(b != null) {
                b.breakNaturally();
            }
        }
    }
}
