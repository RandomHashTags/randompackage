package me.randomhashtags.randompackage.addons.active;

import me.randomhashtags.randompackage.addons.Booster;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class ActiveBooster {
    private final UUID uuid;
    private final OfflinePlayer activator;
    private Booster booster;
    private double multiplier;
    private long expiration;
    public ActiveBooster(OfflinePlayer activator, Booster booster, double multiplier, long expiration) {
        uuid = UUID.randomUUID();
        this.activator = activator;
        this.booster = booster;
        this.multiplier = multiplier;
        this.expiration = expiration;
    }
    public UUID getUUID() { return uuid; }
    public OfflinePlayer getActivator() { return activator; }
    public Booster getBooster() { return booster; }
    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
    public long getExpiration() { return expiration; }
    public void setExpiration(long expiration) { this.expiration = expiration; }
}
