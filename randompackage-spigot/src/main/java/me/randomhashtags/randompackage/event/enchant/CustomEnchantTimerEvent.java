package me.randomhashtags.randompackage.event.enchant;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class CustomEnchantTimerEvent extends RPEventCancellable {
    private final LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants;
    public CustomEnchantTimerEvent(Player player, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        super(player);
        this.enchants = enchants;
    }
    public LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> getEnchants() {
        return enchants;
    }
}
