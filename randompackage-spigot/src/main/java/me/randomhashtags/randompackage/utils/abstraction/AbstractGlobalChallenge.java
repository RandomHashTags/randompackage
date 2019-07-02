package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import me.randomhashtags.randompackage.utils.classes.globalchallenges.ActiveGlobalChallenge;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractGlobalChallenge extends AbstractRPFeature {
    public static HashMap<NamespacedKey, AbstractGlobalChallenge> challenges;
    private Set<UUID> participants;

    public void created(NamespacedKey key) {
        if(challenges == null) challenges = new HashMap<>();
        challenges.put(key, this);
    }

    public abstract ItemStack getDisplayItem();
    public Set<UUID> getParticipants() { return participants; }
    public void setParticipants(Set<UUID> participants) { this.participants = participants; }
    public abstract String getTracks();
    public abstract long getDuration();
    public abstract String getType();

    public ActiveGlobalChallenge start() {
        return start(System.currentTimeMillis(), new HashMap<>());
    }
    public ActiveGlobalChallenge start(long started, HashMap<UUID, BigDecimal> participants) {
        final HashMap<AbstractGlobalChallenge, ActiveGlobalChallenge> a = ActiveGlobalChallenge.active;
        return a != null ? a.getOrDefault(this, new ActiveGlobalChallenge(started, this, participants)) : new ActiveGlobalChallenge(started, this, participants);
    }
    public boolean isActive() {
        final HashMap<AbstractGlobalChallenge, ActiveGlobalChallenge> a = ActiveGlobalChallenge.active;
        return a != null && a.containsKey(this);
    }
}
