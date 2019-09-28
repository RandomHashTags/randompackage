package me.randomhashtags.randompackage.event.lootbag;

import me.randomhashtags.randompackage.addon.Lootbag;
import org.bukkit.entity.Player;

public class DungeonLootbagClaimEvent extends LootbagClaimEvent {
    public DungeonLootbagClaimEvent(Player player, Lootbag lootbag, int rewardSize) {
        super(player, lootbag, rewardSize);
    }
}
