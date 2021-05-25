package me.randomhashtags.randompackage.event.lootbag;

import me.randomhashtags.randompackage.addon.Lootbag;
import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;

public abstract class LootbagClaimEvent extends RPEventCancellable {
    private final Lootbag lootbag;
    private int rewardSize;
    public LootbagClaimEvent(Player player, Lootbag lootbag, int rewardSize) {
        super(player);
        this.lootbag = lootbag;
        this.rewardSize = rewardSize;
    }
    public Lootbag getLootbag() { return lootbag; }
    public int getRewardSize() { return rewardSize; }
    public void setRewardSize(int size) { this.rewardSize = size; }
}
