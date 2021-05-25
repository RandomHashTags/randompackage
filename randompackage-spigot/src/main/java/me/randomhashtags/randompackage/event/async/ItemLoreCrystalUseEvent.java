package me.randomhashtags.randompackage.event.async;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public final class ItemLoreCrystalUseEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;
    private final Player player;
    private final ItemStack item;
    private String msg;
    public ItemLoreCrystalUseEvent(Player player, ItemStack item, String msg) {
        super(true);
        this.player = player;
        this.item = item;
        this.msg = msg;
    }
    public Player getPlayer() { return player; }
    public ItemStack getItem() { return item; }
    public String getMessage() { return msg; }
    public void setMessage(String msg) { this.msg = msg; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
