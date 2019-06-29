package me.randomhashtags.randompackage.api.events.customboss;

import me.randomhashtags.randompackage.utils.classes.living.LivingCustomMinion;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomMinionDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public final LivingCustomMinion minion;
    public final Event damagecause;
    public CustomMinionDeathEvent(LivingCustomMinion minion, Event damagecause) {
        this.minion = minion;
        this.damagecause = damagecause;
    }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
