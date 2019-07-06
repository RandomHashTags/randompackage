package me.randomhashtags.randompackage.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class PlayerArmorEvent extends AbstractEvent implements Cancellable {
	private boolean cancelled;
	public final Player player;
	public final ArmorEventReason reason;
	private final ItemStack item;
	private ItemStack currentItem, cursor;
	
	public PlayerArmorEvent(Player player, ArmorEventReason reason, ItemStack item) {
		this.player = player;
		this.reason = reason;
		this.item = item;
		this.cancelled = false;
	}
	public enum ArmorEventReason {
		DROP,
		BREAK,
		HOTBAR_EQUIP,
		HOTBAR_SWAP,
		INVENTORY_EQUIP,
		INVENTORY_UNEQUIP,
		SHIFT_EQUIP,
		SHIFT_UNEQUIP,
		NUMBER_KEY_EQUIP,
		NUMBER_KEY_UNEQUIP,
	}
	
	public ItemStack getItem() { return item.clone(); }
	public ItemStack getCurrentItem() { return currentItem != null ? currentItem.clone() : null; }
	public void setCurrentItem(ItemStack currentItem) { this.currentItem = currentItem; }
	public ItemStack getCursor() { return cursor != null ? cursor.clone() : null; }
	public void setCursor(ItemStack cursor) { this.cursor = cursor; }

	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}
