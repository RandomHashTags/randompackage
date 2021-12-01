package me.randomhashtags.randompackage.event.armor;

import me.randomhashtags.randompackage.event.enums.ArmorEventReason;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class ArmorEquipEvent extends ArmorEvent {
    public ArmorEquipEvent(Player player, EquipmentSlot slot, ArmorEventReason reason, ItemStack item) {
        super(player, slot, reason, item);
    }
}
