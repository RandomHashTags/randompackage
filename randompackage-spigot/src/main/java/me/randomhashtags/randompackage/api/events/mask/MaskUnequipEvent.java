package me.randomhashtags.randompackage.api.events.mask;

import me.randomhashtags.randompackage.api.events.PlayerArmorEvent;
import me.randomhashtags.randompackage.utils.classes.Mask;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class MaskUnequipEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    public final Player player;
    public final Mask mask;
    public final ItemStack helmet;
    public final PlayerArmorEvent.ArmorEventReason reason;
    public MaskUnequipEvent(Player player, Mask mask, ItemStack helmet, PlayerArmorEvent.ArmorEventReason reason) {
        this.player = player;
        this.mask = mask;
        this.helmet = helmet;
        this.reason = reason;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
