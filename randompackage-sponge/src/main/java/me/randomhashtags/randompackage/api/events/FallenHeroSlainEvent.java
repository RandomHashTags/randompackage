package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.utils.classes.kits.LivingFallenHero;
import org.spongepowered.api.entity.living.Living;

public class FallenHeroSlainEvent extends RandomPackageEvent {
    public final Living killer;
    public final LivingFallenHero hero;
    public final boolean didDropGem;
    public FallenHeroSlainEvent(Living killer, LivingFallenHero hero, boolean didDropGem) {
        this.killer = killer;
        this.hero = hero;
        this.didDropGem = didDropGem;
    }
}
