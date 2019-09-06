package me.randomhashtags.randompackage.events.armor;

import me.randomhashtags.randompackage.events.AbstractCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorEvent extends AbstractCancellable {
    private Player player;
    private ArmorEventReason reason;
    private ItemStack item, currentItem, cursor;
    public ArmorEvent(Player player, ArmorEventReason reason, ItemStack item) {
        this.player = player;
        this.reason = reason;
        this.item = item;
    }
    public Player getPlayer() { return player; }
    public ArmorEventReason getReason() { return reason; }
    public ItemStack getItem() { return item; }

    public ItemStack getCurrentItem() { return currentItem != null ? currentItem.clone() : null; }
    public void setCurrentItem(ItemStack currentItem) { this.currentItem = currentItem; }
    public ItemStack getCursor() { return cursor != null ? cursor.clone() : null; }
    public void setCursor(ItemStack cursor) { this.cursor = cursor; }
}
