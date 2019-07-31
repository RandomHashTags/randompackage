package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.active.ActiveGlobalChallenge;
import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.utils.RPAddon;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public abstract class GlobalChallenge extends RPAddon implements Itemable {
    private Set<UUID> participants;
    public Set<UUID> getParticipants() { return participants; }
    public void setParticipants(Set<UUID> participants) { this.participants = participants; }
    public abstract String getTracks();
    public abstract long getDuration();
    public abstract String getType();

    public ActiveGlobalChallenge start() {
        return start(System.currentTimeMillis(), new HashMap<>());
    }
    public ActiveGlobalChallenge start(long started, HashMap<UUID, BigDecimal> participants) {
        final HashMap<GlobalChallenge, ActiveGlobalChallenge> a = ActiveGlobalChallenge.active;
        return a != null ? a.getOrDefault(this, new ActiveGlobalChallenge(started, this, participants)) : new ActiveGlobalChallenge(started, this, participants);
    }
    public boolean isActive() {
        final HashMap<GlobalChallenge, ActiveGlobalChallenge> a = ActiveGlobalChallenge.active;
        return a != null && a.containsKey(this);
    }
}
