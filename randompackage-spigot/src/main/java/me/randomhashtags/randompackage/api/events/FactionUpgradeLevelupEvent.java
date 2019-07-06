package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.addons.FactionUpgrade;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class FactionUpgradeLevelupEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Player player;
    public final FactionUpgrade upgrade;
    public final int fromTier;
    public FactionUpgradeLevelupEvent(Player player, FactionUpgrade upgrade, int fromTier) {
        this.player = player;
        this.upgrade = upgrade;
        this.fromTier = fromTier;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
