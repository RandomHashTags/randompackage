package me.randomhashtags.randompackage.addon.stats;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class DuelStats {
    private boolean requests;
    private BigDecimal elo;
    private HashMap<ItemStack, List<CustomEnchant>> godset;
    public DuelStats(boolean requests, BigDecimal elo) {
        this(requests, elo, new HashMap<>());
    }
    public DuelStats(boolean requests, BigDecimal elo, HashMap<ItemStack, List<CustomEnchant>> godset) {
        this.requests = requests;
        this.elo = elo;
        this.godset = godset;
    }
    public boolean receivesRequests() { return requests; }
    public void setReceivesRequests(boolean requests) { this.requests = requests; }

    public BigDecimal getELO() { return elo; }
    public void setELO(BigDecimal elo) { this.elo = elo; }
    public HashMap<ItemStack, List<CustomEnchant>> getGodset() { return godset; }
    public void setGodset(HashMap<ItemStack, List<CustomEnchant>> godset) { this.godset = godset; }
    public void setGodsetItem(ItemStack is, List<CustomEnchant> enchants) {
        godset.put(is, enchants);
    }
}
