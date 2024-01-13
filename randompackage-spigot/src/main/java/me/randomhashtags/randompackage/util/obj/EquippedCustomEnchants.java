package me.randomhashtags.randompackage.util.obj;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.event.armor.ArmorEquipEvent;
import me.randomhashtags.randompackage.util.Versionable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;

public final class EquippedCustomEnchants implements Versionable {
    public static HashMap<Player, ArmorEquipEvent> EVENTS = new HashMap<>();
    private final Player player;
    private final LinkedHashMap<EquipmentSlot, LinkedHashMap<CustomEnchantSpigot, Integer>> enchants;

    public EquippedCustomEnchants(@NotNull Player player) {
        this.player = player;
        enchants = new LinkedHashMap<>();
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public void clear() {
        enchants.clear();
    }

    @NotNull
    public LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchantSpigot, Integer>> getInfo() {
        return getInfo(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND);
    }
    @NotNull
    public LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchantSpigot, Integer>> getInfo(EquipmentSlot...slots) {
        final LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchantSpigot, Integer>> map = new LinkedHashMap<>();
        for(EquipmentSlot slot : slots) {
            final ItemStack target = getItemInSlot(slot);
            if(target != null && enchants.containsKey(slot)) {
                map.put(target, enchants.get(slot));
            }
        }
        return map;
    }

    @NotNull
    public LinkedHashMap<EquipmentSlot, LinkedHashMap<CustomEnchantSpigot, Integer>> getEnchants() {
        return enchants;
    }

    @Nullable
    public ItemStack getItem(EquipmentSlot slot) {
        return getItem(slot, false);
    }
    @Nullable
    public ItemStack getItem(EquipmentSlot slot, boolean getEventItem) {
        return getItemInSlot(slot, getEventItem);
    }
    @Nullable
    public ItemStack getEventItem() {
        final ArmorEquipEvent event = EVENTS.getOrDefault(player, null);
        return event != null ? event.getItem() : null;
    }
    @Nullable
    public LinkedHashMap<CustomEnchantSpigot, Integer> getEnchantsOn(EquipmentSlot slot) {
        return enchants.getOrDefault(slot, null);
    }

    public void update(@NotNull EquipmentSlot slot, @Nullable ItemStack withItem) {
        enchants.remove(slot);
        if(withItem != null) {
            enchants.put(slot, CustomEnchants.INSTANCE.getEnchantsOnItem(withItem));
        }
    }
    public void update(@NotNull EquipmentSlot...slots) {
        for(EquipmentSlot slot : slots) {
            update(slot);
        }
    }
    private void update(@NotNull EquipmentSlot slot) {
        final ItemStack is = getItemInSlot(slot);
        update(slot, is);
    }

    @Nullable
    private ItemStack getItemInSlot(EquipmentSlot slot) {
        return getItemInSlot(slot, false);
    }
    @Nullable
    private ItemStack getItemInSlot(@NotNull EquipmentSlot slot, boolean getEventItem) {
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
