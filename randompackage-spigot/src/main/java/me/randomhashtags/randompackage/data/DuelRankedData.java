package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface DuelRankedData {
    BigDecimal getELO();
    void setELO(@NotNull BigDecimal elo);
    HashMap<ItemStack, List<CustomEnchantSpigot>> getGodset();
}
