package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.events.AbstractCancellable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;

public class CEAApplyPotionEffectEvent extends AbstractCancellable {
	public final Event event;
	public final Player player;
	public final LivingEntity appliedto;
	public final CustomEnchant enchant;
	public final PotionEffect potioneffect;
	public final int enchantlevel;
	public CEAApplyPotionEffectEvent(Event event, Player player, LivingEntity appliedto, CustomEnchant enchant, int enchantlevel, PotionEffect potioneffect) {
		this.event = event;
		this.player = player;
		this.appliedto = appliedto;
		this.enchant = enchant;
		this.enchantlevel = enchantlevel;
		this.potioneffect = potioneffect;
	}
}