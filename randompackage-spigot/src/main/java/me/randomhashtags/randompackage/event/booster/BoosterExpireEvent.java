package me.randomhashtags.randompackage.event.booster;

import me.randomhashtags.randompackage.addon.living.ActiveBooster;
import me.randomhashtags.randompackage.event.AbstractEvent;

public class BoosterExpireEvent extends AbstractEvent {
    public final ActiveBooster booster;
    public BoosterExpireEvent(ActiveBooster booster) {
        this.booster = booster;
    }
}
