package me.randomhashtags.randompackage.api.events.customenchant;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemNameTagUseEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
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
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
