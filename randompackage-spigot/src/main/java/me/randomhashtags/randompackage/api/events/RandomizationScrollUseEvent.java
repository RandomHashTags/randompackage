package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.usingpath.PathRandomizationScroll;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class RandomizationScrollUseEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Player player;
    public final CustomEnchant customenchant;
    public final int enchantlevel;
    public final PathRandomizationScroll scroll;
    private int newSuccess, newDestroy;
    public RandomizationScrollUseEvent(Player player, CustomEnchant customenchant, int enchantlevel, PathRandomizationScroll scroll, int newSuccess, int newDestroy) {
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
