package me.randomhashtags.randompackage.util.obj;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.event.armor.ArmorEquipEvent;
import me.randomhashtags.randompackage.util.Versionable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class EquippedCustomEnchants implements Versionable {
    public static HashMap<Player, ArmorEquipEvent> EVENTS = new HashMap<>();
    private Player player;
    private LinkedHashMap<EquipmentSlot, LinkedHashMap<CustomEnchant, Integer>> enchants;

    public EquippedCustomEnchants(@NotNull Player player) {
        this.player = player;
        enchants = new LinkedHashMap<>();
    }

    public Player getPlayer() { return player; }

    public void clear() {
        enchants.clear();
    }
    public LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> getInfo() {
        return getInfo(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND);
    }
    public LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> getInfo(EquipmentSlot...slots) {
        final LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> map = new LinkedHashMap<>();
        for(EquipmentSlot slot : slots) {
            final ItemStack target = getItemInSlot(slot);
            if(target != null && enchants.containsKey(slot)) {
                map.put(target, enchants.get(slot));
            }
        }
        return map;
    }

    public LinkedHashMap<EquipmentSlot, LinkedHashMap<CustomEnchant, Integer>> getEnchants() { return enchants; }

    public ItemStack getItem(EquipmentSlot slot) {
        return getItem(slot, false);
    }
    public ItemStack getItem(EquipmentSlot slot, boolean getEventItem) {
        return getItemInSlot(slot, getEventItem);
    }
    @Nullable
    public ItemStack getEventItem() {
        final ArmorEquipEvent event = EVENTS.getOrDefault(player, null);
        return event != null ? event.getItem() : null;
    }
    @Nullable
    public LinkedHashMap<CustomEnchant, Integer> getEnchantsOn(EquipmentSlot slot) {
        return enchants.getOrDefault(slot, null);
    }

    public void update(EquipmentSlot slot, ItemStack withItem) {
        if(slot != null) {
            enchants.remove(slot);
            if(withItem != null) {
                enchants.put(slot, CustomEnchants.getCustomEnchants().getEnchantsOnItem(withItem));
            }
        }
    }
    public void update(EquipmentSlot...slots) {
        for(EquipmentSlot slot : slots) {
            if(slot != null) {
                update(slot);
            }
        }
    }
    private void update(EquipmentSlot slot) {
        final ItemStack is = getItemInSlot(slot);
        update(slot, is);
    }

    private ItemStack getItemInSlot(EquipmentSlot slot) { return getItemInSlot(slot, false); }
    private ItemStack getItemInSlot(EquipmentSlot slot, boolean getEventItem) {
        if(slot == null) { return null; }
        if(getEventItem && EVENTS.containsKey(player)) {
            return EVENTS.get(player).getItem();
        }
        final PlayerInventory inv = player.getInventory();
        switch (slot) {
            case HEAD: return inv.getHelmet();
            case CHEST: return inv.getChestplate();
            case LEGS: return inv.getLeggings();
            case FEET: return inv.getBoots();
            case HAND: return EIGHT ? inv.getItemInHand() : inv.getItemInMainHand();
            default: return EIGHT ? null : inv.getItemInOffHand();
        }
    }
}
