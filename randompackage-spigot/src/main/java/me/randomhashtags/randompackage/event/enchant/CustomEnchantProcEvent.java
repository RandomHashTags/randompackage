package me.randomhashtags.randompackage.event.enchant;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.event.AbstractCancellable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CustomEnchantProcEvent extends AbstractCancellable {
	private Event event;
	private Player player;
	private HashMap<String, Entity> entities;
	private CustomEnchant enchant;
	private int level;
	private ItemStack itemWithEnchant;
	public CustomEnchantProcEvent(Player player, Event event, HashMap<String, Entity> entities, CustomEnchant enchant, int level, ItemStack itemWithEnchant) {
		this.event = event;
		this.player = player;
		this.entities = entities;
		this.enchant = enchant;
		this.level = level;
		this.itemWithEnchant = itemWithEnchant;
	}
	public Event getEvent() { return event; }
	public Player getHolder() { return player; }
	public HashMap<String, Entity> getEntities() { return entities; }
	public CustomEnchant getEnchant() { return enchant; }
	public int getEnchantLevel() { return level; }
	public ItemStack getItemWithEnchant() { return itemWithEnchant; }

	public Entity getEntity(String identifier) { return entities.getOrDefault(identifier, null); }
	public Entity getEntity() { return getEntity("Entity"); }
	public Entity getPlayer() { return getEntity("Player"); }
	public Entity getDamager() { return getEntity("Damager"); }
	public Entity getVictim() { return getEntity("Victim"); }
	public Entity getShooter() { return getEntity("Shooter"); }
	public Entity getProjectile() { return getEntity("Projectile"); }
	public Entity getCaught() { return getEntity("Caught"); }
	public Entity getWinner() { return getEntity("Winner"); }
	public Entity getLoser() { return getEntity("Loser"); }
}
