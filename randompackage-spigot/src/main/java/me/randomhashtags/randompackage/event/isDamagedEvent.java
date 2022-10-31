package me.randomhashtags.randompackage.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public final class isDamagedEvent extends DamageEvent {
    private final Player victim;
    private final LivingEntity damager;
    public isDamagedEvent(@NotNull Player victim, @NotNull LivingEntity damager, double damage) {
        super(damager, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
        this.victim = victim;
        this.damager = damager;
    }
    public isDamagedEvent(Player victim, EntityDamageEvent.DamageCause cause, double damage) {
        super(null, victim, cause, damage);
        this.victim = victim;
        this.damager = null;
    }
    @NotNull
    @Override
    public Player getEntity() {
        return victim;
    }
    @Override
    public LivingEntity getDamager() {
        return damager;
    }
}
