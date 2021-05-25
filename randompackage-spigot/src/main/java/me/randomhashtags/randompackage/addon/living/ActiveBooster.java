package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.Booster;
import me.randomhashtags.randompackage.addon.BoosterRecipients;
import me.randomhashtags.randompackage.event.booster.BoosterActivateEvent;
import me.randomhashtags.randompackage.event.booster.BoosterExpireEvent;
import me.randomhashtags.randompackage.universal.UVersionable;
import org.bukkit.OfflinePlayer;

public final class ActiveBooster implements UVersionable {
    private int task;
    private final OfflinePlayer activator;
    private final String faction;
    private final Booster booster;
    private double multiplier;
    private final long duration;
    private long expiration;
    public ActiveBooster(OfflinePlayer activator, Booster booster, double multiplier, long duration, long expiration) {
        this(activator, null, booster, multiplier, duration, expiration);
    }
    public ActiveBooster(BoosterActivateEvent event, String faction, long expiration) {
        this(event.activator, faction, event.booster, event.multiplier, event.duration, expiration);
    }
    public ActiveBooster(BoosterActivateEvent event, long expiration) {
        this(event.activator, null, event.booster, event.multiplier, event.duration, expiration);
    }
    public ActiveBooster(OfflinePlayer activator, String faction, Booster booster, double multiplier, long duration, long expiration) {
        this.activator = activator;
        this.faction = faction;
        this.booster = booster;
        this.multiplier = multiplier;
        this.duration = duration;
        this.expiration = expiration;
        task = -1;
        updateTask();
    }
    public OfflinePlayer getActivator() {
        return activator;
    }
    public String getFaction() {
        return faction;
    }
    public BoosterRecipients getRecipients() {
        return booster.getRecipients();
    }
    public Booster getBooster() {
        return booster;
    }
    public double getMultiplier() {
        return multiplier;
    }
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
    public long getDuration() {
        return duration;
    }
    public long getExpiration() {
        return expiration;
    }
    public void setExpiration(long expiration) {
        this.expiration = expiration;
        updateTask();
    }
    public long getRemainingTime() {
        return expiration-System.currentTimeMillis();
    }
    private void updateTask() {
        if(task != -1) {
            SCHEDULER.cancelTask(task);
        }
        SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
            final BoosterExpireEvent e = new BoosterExpireEvent(this);
            PLUGIN_MANAGER.callEvent(e);
        }, (getRemainingTime()/1000)*20);
    }
    public void expire(boolean callEvent) {
        nullify(callEvent);
    }
    private void nullify(boolean callEvent) {
        if(callEvent) {
            final BoosterExpireEvent e = new BoosterExpireEvent(this);
            PLUGIN_MANAGER.callEvent(e);
        }
        if(task != -1) {
            SCHEDULER.cancelTask(task);
            task = -1;
        }
    }
}
