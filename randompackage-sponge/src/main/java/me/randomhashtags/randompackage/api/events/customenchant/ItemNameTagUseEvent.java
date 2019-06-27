package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.item.inventory.ItemStack;

public class ItemNameTagUseEvent extends RandomPackageEvent implements Cancellable {
    private boolean cancelled;
    public final Player player;
    public final ItemStack item;
    public String renamedTo;
    public ItemNameTagUseEvent(Player player, ItemStack item, String renamedTo) {
        this.player = player;
        this.item = item;
        this.renamedTo = renamedTo;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
