package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.impl.AbstractEvent;

public class CEAApplyPotionEffectEvent extends RandomPackageEvent implements Cancellable {
	public final AbstractEvent event;
	public final Player player;
	public final Living appliedto;
	public final CustomEnchant enchant;
	public final PotionEffect potioneffect;
	private boolean cancelled;
	public final int enchantlevel;
	public CEAApplyPotionEffectEvent(AbstractEvent event, Player player, Living appliedto, CustomEnchant enchant, int enchantlevel, PotionEffect potioneffect) {
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