package me.randomhashtags.randompackage.addons.active;

import me.randomhashtags.randompackage.addons.Booster;
import me.randomhashtags.randompackage.api.events.BoosterActivateEvent;
import me.randomhashtags.randompackage.api.events.BoosterExpireEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitScheduler;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class ActiveBooster {
    private int task;
    private OfflinePlayer activator;
    private String faction;
    private Booster booster;
    private double multiplier;
    private long duration, expiration;
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
    public OfflinePlayer getActivator() { return activator; }
    public String getFaction() { return faction; }
    public boolean isFactionBooster() { return faction != null && booster.getRecipients().equalsIgnoreCase("faction_members"); }
    public boolean isSelfBooster() { return faction == null && booster.getRecipients().equalsIgnoreCase("self"); }
    public Booster getBooster() { return booster; }
    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
    public long getDuration() { return duration; }
    public long getExpiration() { return expiration; }
    public void setExpiration(long expiration) {
        this.expiration = expiration;
        updateTask();
    }
    public long getRemainingTime() { return expiration-System.currentTimeMillis(); }
    private void updateTask() {
        final BukkitScheduler s = Bukkit.getScheduler();
        if(task != -1) s.cancelTask(task);
        s.scheduleSyncDelayedTask(getPlugin, () -> {
            final BoosterExpireEvent e = new BoosterExpireEvent(this);
            Bukkit.getPluginManager().callEvent(e);
        }, (getRemainingTime()/1000)*20);
    }
    public void expire(boolean callEvent) { nullify(callEvent); }
    private void nullify(boolean callEvent) {
        if(callEvent) {
            final BoosterExpireEvent e = new BoosterExpireEvent(this);
            Bukkit.getPluginManager().callEvent(e);
        }
        if(task != -1) {
            Bukkit.getScheduler().cancelTask(task);
            task = -1;
        }
        activator = null;
        faction = null;
        booster = null;
        multiplier = 0;
        duration = 0;
        expiration = 0;
    }
}
