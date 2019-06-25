package me.randomhashtags.randompackage.api.events.customboss;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.custombosses.LivingCustomMinion;
import org.spongepowered.api.event.impl.AbstractEvent;

public class CustomMinionDeathEvent extends RandomPackageEvent {
    public final LivingCustomMinion minion;
    public final AbstractEvent damagecause;
    public CustomMinionDeathEvent(LivingCustomMinion minion, AbstractEvent damagecause) {
        this.minion = minion;
        this.damagecause = damagecause;
    }
}
