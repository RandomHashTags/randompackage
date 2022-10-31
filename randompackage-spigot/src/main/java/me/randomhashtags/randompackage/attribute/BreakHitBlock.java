package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public final class BreakHitBlock extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final Event event = pending.getEvent();
        if(event instanceof PlayerInteractEvent) {
            final PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
            final Block block = interactEvent.getClickedBlock();
            if(block != null) {
                block.breakNaturally();
            }
        }
    }
}
