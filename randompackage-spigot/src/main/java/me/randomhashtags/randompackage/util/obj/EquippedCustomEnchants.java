package me.randomhashtags.randompackage.util.obj;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.api.CustomEnchants;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.LinkedHashMap;

public class EquippedCustomEnchants {
    private Player player;
    private LinkedHashMap<EquipmentSlot, ItemStack> items;
    private LinkedHashMap<EquipmentSlot, LinkedHashMap<CustomEnchant, Integer>> enchants;

    public EquippedCustomEnchants(@NotNull Player player) {
        this.player = player;
        items = new LinkedHashMap<>();
        enchants = new LinkedHashMap<>();
    }

    public void clear() {
        items.clear();
        enchants.clear();
    }
    public LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> getEnchants() {
        return getEnchants(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND);
    }
    public LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> getEnchants(EquipmentSlot...slots) {
        final LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> map = new LinkedHashMap<>();
        for(EquipmentSlot slot : slots) {
            map.put(items.get(slot), enchants.get(slot));
        }
        return map;
    }
    public ItemStack getItem(EquipmentSlot slot) {
        return items.getOrDefault(slot, null);
    }
    public LinkedHashMap<CustomEnchant, Integer> getEnchantsOn(EquipmentSlot slot) {
        return enchants.getOrDefault(slot, new LinkedHashMap<>());
    }
    public void setEnchantsOn(EquipmentSlot slot, LinkedHashMap<CustomEnchant, Integer> enchants) {
        this.enchants.put(slot, enchants);
    }

    public void update(EquipmentSlot slot, ItemStack withItem) {
        items.put(slot, withItem);
        enchants.remove(slot);
        if(withItem != null) {
            enchants.put(slot, CustomEnchants.getCustomEnchants().getEnchantsOnItem(withItem));
        }
    }
    public void update(EquipmentSlot...slots) {
        for(EquipmentSlot slot : slots) {
            update(slot);
        }
    }
    private void update(EquipmentSlot slot) {
        final PlayerInventory inv = player.getInventory();
        final ItemStack is;
        switch (slot) {
            case HEAD:
                is = inv.getHelmet();
                break;
            case CHEST:
                is = inv.getChestplate();
                break;
            case LEGS:
                is = inv.getLeggings();
                break;
            case FEET:
                is = inv.getBoots();
                break;
            case HAND:
                is = inv.getItemInMainHand();
                break;
            case OFF_HAND:
                is = inv.getItemInOffHand();
                break;
            default:
                return;
        }
        update(slot, is);
    }
}
