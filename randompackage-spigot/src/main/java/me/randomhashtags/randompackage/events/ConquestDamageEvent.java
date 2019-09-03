package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.LivingConquestChest;
import org.bukkit.entity.Player;

public class ConquestDamageEvent extends AbstractCancellable {
    public final Player damager;
    public final LivingConquestChest livingConquestChest;
    public double damage;
    public ConquestDamageEvent(Player damager, LivingConquestChest livingConquestChest, double damage) {
        this.damager = damager;
        this.livingConquestChest = livingConquestChest;
        this.damage = damage;
    }
}
