package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.FactionUpgrade;
import org.bukkit.entity.Player;

public final class FactionUpgradeLevelupEvent extends RPEventCancellable {
    public final FactionUpgrade upgrade;
    public final int fromTier;
    public FactionUpgradeLevelupEvent(Player player, FactionUpgrade upgrade, int fromTier) {
        super(player);
        this.upgrade = upgrade;
        this.fromTier = fromTier;
    }
}
