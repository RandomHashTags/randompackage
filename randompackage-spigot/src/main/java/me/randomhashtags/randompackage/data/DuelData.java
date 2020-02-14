package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface DuelData {
    boolean receivesRequests();
    BigDecimal getELO();
    HashMap<ItemStack, List<CustomEnchant>> getGodset();
}
