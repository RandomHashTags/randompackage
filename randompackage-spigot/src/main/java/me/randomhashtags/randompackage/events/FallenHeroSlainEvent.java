package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.LivingFallenHero;
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
