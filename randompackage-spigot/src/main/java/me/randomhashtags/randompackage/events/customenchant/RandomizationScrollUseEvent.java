package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.addons.objects.customenchants.RandomizationScroll;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class RandomizationScrollUseEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Player player;
    public final AbstractCustomEnchant customenchant;
    public final int enchantlevel;
    public final RandomizationScroll scroll;
    private int newSuccess, newDestroy;
    public RandomizationScrollUseEvent(Player player, AbstractCustomEnchant customenchant, int enchantlevel, RandomizationScroll scroll, int newSuccess, int newDestroy) {
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
}
