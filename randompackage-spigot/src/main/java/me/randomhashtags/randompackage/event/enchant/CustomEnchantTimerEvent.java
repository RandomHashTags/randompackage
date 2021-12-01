package me.randomhashtags.randompackage.event.enchant;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class CustomEnchantTimerEvent extends RPEventCancellable {
    private final LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchantSpigot, Integer>> enchants;
    public CustomEnchantTimerEvent(Player player, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchantSpigot, Integer>> enchants) {
        super(player);
        this.enchants = enchants;
    }
    public LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchantSpigot, Integer>> getEnchants() {
        return enchants;
    }
}
