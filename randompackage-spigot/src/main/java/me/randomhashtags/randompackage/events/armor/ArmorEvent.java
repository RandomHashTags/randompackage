package me.randomhashtags.randompackage.events.armor;

import me.randomhashtags.randompackage.events.RPEventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorEvent extends RPEventCancellable {
    private ArmorEventReason reason;
    private ItemStack item, currentItem, cursor;
    public ArmorEvent(Player player, ArmorEventReason reason, ItemStack item) {
        super(player);
        this.reason = reason;
        this.item = item;
    }
    public ArmorEventReason getReason() { return reason; }
    public ItemStack getItem() { return item; }

    public ItemStack getCurrentItem() { return currentItem != null ? currentItem.clone() : null; }
    public void setCurrentItem(ItemStack currentItem) { this.currentItem = currentItem; }
    public ItemStack getCursor() { return cursor != null ? cursor.clone() : null; }
    public void setCursor(ItemStack cursor) { this.cursor = cursor; }
}
