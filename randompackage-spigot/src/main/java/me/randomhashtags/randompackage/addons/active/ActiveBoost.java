package me.randomhashtags.randompackage.addons.active;

import me.randomhashtags.randompackage.addons.Booster;

import java.util.UUID;

public class ActiveBoost {
    private final UUID uuid;
    private Booster booster;
    private double multiplier;
    private long expiration;
    public ActiveBoost(Booster booster, double multiplier, long expiration) {
        uuid = UUID.randomUUID();
        this.booster = booster;
        this.multiplier = multiplier;
        this.expiration = expiration;
    }
    public UUID getUUID() { return uuid; }
    public Booster getBooster() { return booster; }
    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
    public long getExpiration() { return expiration; }
    public void setExpiration(long expiration) { this.expiration = expiration; }
}
