package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class EquipmentLootboxOpenEvent extends RPEventCancellable {
    private final ItemStack reward;
    public EquipmentLootboxOpenEvent(Player player, ItemStack reward) {
        super(player);
        this.reward = reward;
    }
    public ItemStack getReward() {
        return reward;
    }
}
