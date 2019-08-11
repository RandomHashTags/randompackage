package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class ItemNameTagUseEvent extends AbstractEvent implements Cancellable {
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
