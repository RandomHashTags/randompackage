package me.randomhashtags.randompackage.event.mob;

import me.randomhashtags.randompackage.addon.living.LivingCustomMinion;
import me.randomhashtags.randompackage.event.AbstractEvent;
import org.bukkit.event.Event;

public class CustomMinionDeathEvent extends AbstractEvent {
    public final LivingCustomMinion minion;
    public final Event damagecause;
    public CustomMinionDeathEvent(LivingCustomMinion minion, Event damagecause) {
        this.minion = minion;
        this.damagecause = damagecause;
    }
}
