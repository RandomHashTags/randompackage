package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.living.LivingConquestChest;
import org.bukkit.entity.Player;

public class ConquestDamageEvent extends RPEventCancellable {
    public final LivingConquestChest livingConquestChest;
    public double damage;
    public ConquestDamageEvent(Player damager, LivingConquestChest livingConquestChest, double damage) {
        super(damager);
        this.livingConquestChest = livingConquestChest;
        this.damage = damage;
    }
}
