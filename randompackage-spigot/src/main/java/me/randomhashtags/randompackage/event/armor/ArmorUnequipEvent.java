package me.randomhashtags.randompackage.event.armor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ArmorUnequipEvent extends ArmorEvent {
    public ArmorUnequipEvent(Player player, EquipmentSlot slot, ArmorEventReason reason, ItemStack item) {
        super(player, slot, reason, item);
    }
}
