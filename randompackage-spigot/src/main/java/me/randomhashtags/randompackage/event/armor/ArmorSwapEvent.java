package me.randomhashtags.randompackage.event.armor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ArmorSwapEvent extends ArmorEvent {
    private ItemStack equip, unequip;
    public ArmorSwapEvent(Player player, EquipmentSlot slot, ArmorEventReason reason, ItemStack equip, ItemStack unequip) {
        super(player, slot, reason, null);
    }
    @Override public ItemStack getItem() { return equip; }
    public ItemStack getUnequipped() { return unequip; }
}
