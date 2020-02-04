package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.Scroll;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ScrollUseEvent extends RPEventCancellable {
    protected Scroll scroll;
    private ItemStack usedOnItem;
    public ScrollUseEvent(Player player, Scroll scroll, ItemStack usedOnItem) {
        super(player);
        this.scroll = scroll;
        this.usedOnItem = usedOnItem;
    }
    public Scroll getScroll() {
        return scroll;
    }
    public ItemStack getUsedOnItem() {
        return usedOnItem;
    }
}
