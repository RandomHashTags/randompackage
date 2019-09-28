package me.randomhashtags.randompackage.event.lootbag;

import me.randomhashtags.randompackage.addon.Lootbag;
import org.bukkit.entity.Player;

public class KOTHLootbagClaimEvent extends LootbagClaimEvent {
    public KOTHLootbagClaimEvent(Player player, Lootbag lootbag, int rewardSize) {
        super(player, lootbag, rewardSize);
    }
}
