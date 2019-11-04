package me.randomhashtags.randompackage.event.armor;

import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ArmorEvent extends RPEventCancellable {
    private ArmorEventReason reason;
    private ItemStack item, currentItem, cursor;
    public ArmorEvent(Player player, ArmorEventReason reason, ItemStack item) {
        super(player);
        this.reason = reason;
        this.item = item;
    }
    public ArmorEventReason getReason() { return reason; }
    public ItemStack getItem() { return item; }
    public void setItem(ItemStack is) { item = is; }

    public ItemStack getCurrentItem() { return currentItem != null ? currentItem.clone() : null; }
    public void setCurrentItem(ItemStack currentItem) { this.currentItem = currentItem; }
    public ItemStack getCursor() { return cursor != null ? cursor.clone() : null; }
    public void setCursor(ItemStack cursor) { this.cursor = cursor; }
}
