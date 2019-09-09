package me.randomhashtags.randompackage.event.armor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorUnequipEvent extends ArmorEvent {
    public ArmorUnequipEvent(Player player, ArmorEventReason reason, ItemStack item) {
        super(player, reason, item);
    }
}
