package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import me.randomhashtags.randompackage.utils.classes.customenchants.RandomizationScroll;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RandomizationScrollUseEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    public final Player player;
    public final CustomEnchant customenchant;
    public final int enchantlevel;
    public final RandomizationScroll scroll;
    private int newSuccess, newDestroy;
    public RandomizationScrollUseEvent(Player player, CustomEnchant customenchant, int enchantlevel, RandomizationScroll scroll, int newSuccess, int newDestroy) {
        this.player = player;
        this.customenchant = customenchant;
        this.enchantlevel = enchantlevel;
        this.scroll = scroll;
        this.newSuccess = newSuccess;
        this.newDestroy = newDestroy;
    }
    public int getNewSuccess() { return newSuccess; }
    public void setNewSuccess(int success) { newSuccess = success; }
    public int getNewDestroy() { return newDestroy; }
    public void setNewDestroy(int destroy) { newDestroy = destroy; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
