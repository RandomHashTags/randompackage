package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.living.ActiveBooster;

public class BoosterExpireEvent extends AbstractEvent {
    public final ActiveBooster booster;
    public BoosterExpireEvent(ActiveBooster booster) {
        this.booster = booster;
    }
}
