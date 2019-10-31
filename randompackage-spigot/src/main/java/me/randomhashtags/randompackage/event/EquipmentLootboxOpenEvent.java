package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EquipmentLootboxOpenEvent extends RPEventCancellable {
    private ItemStack reward;
    public EquipmentLootboxOpenEvent(Player player, ItemStack reward) {
        super(player);
        this.reward = reward;
    }
    public ItemStack getReward() { return reward; }
}
