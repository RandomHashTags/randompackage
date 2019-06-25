package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.utils.classes.kits.LivingFallenHero;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FallenHeroSlainEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public final LivingEntity killer;
    public final LivingFallenHero hero;
    public final boolean didDropGem;
    public FallenHeroSlainEvent(LivingEntity killer, LivingFallenHero hero, boolean didDropGem) {
        this.killer = killer;
        this.hero = hero;
        this.didDropGem = didDropGem;
    }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
