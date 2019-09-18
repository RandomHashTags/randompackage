package me.randomhashtags.randompackage.event.enchant;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.event.AbstractCancellable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CustomEnchantProcEvent extends AbstractCancellable {
	private Event event;
	private HashMap<String, Entity> entities;
	private CustomEnchant enchant;
	private int level;
	private ItemStack itemWithEnchant;
	public CustomEnchantProcEvent(Event event, HashMap<String, Entity> entities, CustomEnchant enchant, int level, ItemStack itemWithEnchant) {
		this.event = event;
		this.entities = entities;
		this.enchant = enchant;
		this.level = level;
		this.itemWithEnchant = itemWithEnchant;
	}
	public Event getEvent() { return event; }
	public HashMap<String, Entity> getEntities() { return entities; }
	public CustomEnchant getEnchant() { return enchant; }
	public int getEnchantLevel() { return level; }
	public ItemStack getItemWithEnchant() { return itemWithEnchant; }
}
