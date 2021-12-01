package me.randomhashtags.randompackage.event.armor;

import me.randomhashtags.randompackage.event.enums.ArmorEventReason;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class ArmorPieceBreakEvent extends ArmorEvent {
    public ArmorPieceBreakEvent(Player player, EquipmentSlot slot, ItemStack item) {
        super(player, slot, ArmorEventReason.BREAK, item);
    }
}
