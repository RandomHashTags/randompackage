package me.randomhashtags.randompackage.event.mob;

import me.randomhashtags.randompackage.addon.living.LivingFallenHero;
import me.randomhashtags.randompackage.event.AbstractEvent;
import org.bukkit.entity.LivingEntity;

public class FallenHeroSlainEvent extends AbstractEvent {
    public final LivingEntity killer;
    public final LivingFallenHero hero;
    public final boolean didDropGem;
    public FallenHeroSlainEvent(LivingEntity killer, LivingFallenHero hero, boolean didDropGem) {
        this.killer = killer;
        this.hero = hero;
        this.didDropGem = didDropGem;
    }
}
