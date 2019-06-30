package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomEnchant;
import me.randomhashtags.randompackage.utils.abstraction.AbstractEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;

public class CEAApplyPotionEffectEvent extends AbstractEvent implements Cancellable {
	public final Event event;
	public final Player player;
	public final LivingEntity appliedto;
	public final AbstractCustomEnchant enchant;
	public final PotionEffect potioneffect;
	private boolean cancelled;
	public final int enchantlevel;
	public CEAApplyPotionEffectEvent(Event event, Player player, LivingEntity appliedto, AbstractCustomEnchant enchant, int enchantlevel, PotionEffect potioneffect) {
		this.event = event;
		this.player = player;
		this.appliedto = appliedto;
		this.enchant = enchant;
		this.enchantlevel = enchantlevel;
		this.potioneffect = potioneffect;
		cancelled = false;
	}
	public void setCancelled(boolean cancel) { cancelled = cancel; }
	public boolean isCancelled() { return cancelled; }
}