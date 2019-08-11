package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.active.ActiveBooster;

public class BoosterExpireEvent extends AbstractEvent {
    public final ActiveBooster booster;
    public BoosterExpireEvent(ActiveBooster booster) {
        this.booster = booster;
    }
}
