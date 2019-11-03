package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.living.ActiveGlobalChallenge;
import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Toggleable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

public interface GlobalChallenge extends Attributable, Itemable, Toggleable {
    long getDuration();
    String getType();

    default ActiveGlobalChallenge start() {
        return start(System.currentTimeMillis(), new HashMap<>());
    }
    default ActiveGlobalChallenge start(long started, HashMap<UUID, BigDecimal> participants) {
        final HashMap<GlobalChallenge, ActiveGlobalChallenge> a = ActiveGlobalChallenge.active;
        return a != null ? a.getOrDefault(this, new ActiveGlobalChallenge(started, this, participants)) : new ActiveGlobalChallenge(started, this, participants);
    }
    default boolean isActive() {
        final HashMap<GlobalChallenge, ActiveGlobalChallenge> a = ActiveGlobalChallenge.active;
        return a != null && a.containsKey(this);
    }
}
