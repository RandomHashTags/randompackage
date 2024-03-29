package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public final class HitBlock extends AbstractEventCondition {
    @Override
    public boolean check(@NotNull Event event, @NotNull String value) {
        final PlayerInteractEvent interactEvent = event instanceof PlayerInteractEvent ? (PlayerInteractEvent) event : null;
        if(interactEvent != null) {
            final Block block = interactEvent.getClickedBlock();
            return block != null && block.getType().name().endsWith(value.toUpperCase());
        }
        return false;
    }
}
