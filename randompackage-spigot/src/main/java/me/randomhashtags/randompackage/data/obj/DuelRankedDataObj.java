package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.data.DuelRankedData;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class DuelRankedDataObj implements DuelRankedData {
    private BigDecimal elo;
    private HashMap<ItemStack, List<CustomEnchant>> godset;

    public DuelRankedDataObj(BigDecimal elo, HashMap<ItemStack, List<CustomEnchant>> godset) {
        this.elo = elo;
        this.godset = godset;
    }

    @Override
    public BigDecimal getELO() {
        return elo;
    }

    @Override
    public void setELO(BigDecimal elo) {
        this.elo = elo;
    }

    @Override
    public HashMap<ItemStack, List<CustomEnchant>> getGodset() {
        return godset;
    }
}
