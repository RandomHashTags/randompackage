package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.FactionUpgrade;
import org.bukkit.entity.Player;

public class FactionUpgradeLevelupEvent extends AbstractCancellable {
    public final Player player;
    public final FactionUpgrade upgrade;
    public final int fromTier;
    public FactionUpgradeLevelupEvent(Player player, FactionUpgrade upgrade, int fromTier) {
        this.player = player;
        this.upgrade = upgrade;
        this.fromTier = fromTier;
    }
}
