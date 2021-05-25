package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.living.LivingConquestChest;
import org.bukkit.entity.Player;

public final class ConquestBlockDamageEvent extends RPEventCancellable {
    private final LivingConquestChest conquestChest;
    private double damage;
    public ConquestBlockDamageEvent(Player damager, LivingConquestChest conquestChest, double damage) {
        super(damager);
        this.conquestChest = conquestChest;
        this.damage = damage;
    }
    public LivingConquestChest getConquestChest() {
        return conquestChest;
    }
    public double getDamage() {
        return damage;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }
}
