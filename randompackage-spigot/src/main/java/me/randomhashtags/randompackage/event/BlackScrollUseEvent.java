package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.BlackScroll;
import me.randomhashtags.randompackage.addon.Scroll;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlackScrollUseEvent extends ScrollUseEvent {
    private int successRate;
    public BlackScrollUseEvent(Player player, Scroll scroll, ItemStack usedOnItem, int successRate) {
        super(player, scroll, usedOnItem);
        this.successRate = successRate;
    }
    @Override
    public BlackScroll getScroll() {
        return (BlackScroll) scroll;
    }
    public int getSuccessRate() {
        return successRate;
    }
    public void setSuccessRate(int successRate) {
        this.successRate = successRate;
    }
}
