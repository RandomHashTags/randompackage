package me.randomhashtags.randompackage.events;

public abstract class DamageEvent extends AbstractCancellable {
    private double damage;
    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }
}
