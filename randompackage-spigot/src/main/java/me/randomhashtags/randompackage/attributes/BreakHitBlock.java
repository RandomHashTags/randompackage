package me.randomhashtags.randompackage.attributes;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class BreakHitBlock extends AbstractEventAttribute {
    @Override
    public void execute(Event event) {
        if(event instanceof PlayerInteractEvent) {
            final PlayerInteractEvent e = (PlayerInteractEvent) event;
            final Block b = e.getClickedBlock();
            if(b != null) {
                b.breakNaturally();
            }
        }
    }
}
