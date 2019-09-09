package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.FactionUpgrade;
import org.bukkit.entity.Player;

public class FactionUpgradeLevelupEvent extends RPEventCancellable {
    public final FactionUpgrade upgrade;
    public final int fromTier;
    public FactionUpgradeLevelupEvent(Player player, FactionUpgrade upgrade, int fromTier) {
        super(player);
        this.upgrade = upgrade;
        this.fromTier = fromTier;
    }
}
