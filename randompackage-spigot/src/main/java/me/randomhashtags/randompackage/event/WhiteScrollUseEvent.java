package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.Scroll;
import me.randomhashtags.randompackage.addon.WhiteScroll;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class WhiteScrollUseEvent extends ScrollUseEvent {
    public WhiteScrollUseEvent(Player player, Scroll scroll, ItemStack usedOnItem) {
        super(player, scroll, usedOnItem);
    }
    @Override
    public WhiteScroll getScroll() {
        return (WhiteScroll) scroll;
    }
}
