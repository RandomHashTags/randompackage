package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
import me.randomhashtags.randompackage.recode.api.addons.active.LivingFallenHero;
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
