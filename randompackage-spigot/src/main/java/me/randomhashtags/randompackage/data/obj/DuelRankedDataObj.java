package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.data.DuelRankedData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public final class DuelRankedDataObj implements DuelRankedData {
    private BigDecimal elo;
    private final HashMap<ItemStack, List<CustomEnchantSpigot>> godset;

    public DuelRankedDataObj(BigDecimal elo, HashMap<ItemStack, List<CustomEnchantSpigot>> godset) {
        this.elo = elo;
        this.godset = godset;
    }

    @Override
    public BigDecimal getELO() {
        return elo;
    }

    @Override
    public void setELO(@NotNull BigDecimal elo) {
        this.elo = elo;
    }

    @Override
    public HashMap<ItemStack, List<CustomEnchantSpigot>> getGodset() {
        return godset;
    }
}
