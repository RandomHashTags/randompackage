package me.randomhashtags.randompackage.events.customboss;

import me.randomhashtags.randompackage.addons.active.LivingCustomMinion;
import org.bukkit.event.Event;

public class CustomMinionDeathEvent extends AbstractEvent {
    public final LivingCustomMinion minion;
    public final Event damagecause;
    public CustomMinionDeathEvent(LivingCustomMinion minion, Event damagecause) {
        this.minion = minion;
        this.damagecause = damagecause;
    }
}
