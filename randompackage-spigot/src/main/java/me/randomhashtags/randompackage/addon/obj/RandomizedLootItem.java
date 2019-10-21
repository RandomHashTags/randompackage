package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RandomizedLootItem extends RPStorage implements Itemable {
    private String key, rewardSize;
    private ItemStack item;
    private List<String> rewards;
    public RandomizedLootItem(String identifier, ItemStack item, String rewardSize, List<String> rewards) {
        this.key = identifier;
        this.item = item;
    }
    public String getIdentifier() { return key; }
    public ItemStack getItem() { return getClone(item); }
    public String getRewardSize() { return rewardSize; }
    public List<String> getRewards() { return rewards; }

}
