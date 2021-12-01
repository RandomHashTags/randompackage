package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.addon.RandomizedLoot;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class RandomizedLootItem implements RandomizedLoot, UVersionableSpigot {
    private final String key, rewardSize;
    private final ItemStack item;
    private final List<String> rewards;
    public RandomizedLootItem(String identifier, ItemStack item, String rewardSize, List<String> rewards) {
        this.key = identifier;
        this.item = item;
        this.rewardSize = rewardSize;
        this.rewards = rewards;
    }
    public String getIdentifier() {
        return key;
    }
    public ItemStack getItem() {
        return getClone(item);
    }
    public String getRewardSize() {
        return rewardSize;
    }
    public List<String> getRewards() {
        return rewards;
    }

    public String getRandomReward() {
        return getRandomReward(null);
    }
    public String getRandomReward(List<String> excluding) {
        final List<String> a = new ArrayList<>(rewards);
        if(excluding != null && !excluding.isEmpty()) {
            a.removeAll(excluding);
        }
        return a.get(RANDOM.nextInt(a.size()));
    }
    public int getRandomRewardSize() {
        final String[] values = rewardSize.split("-");
        final int min = Integer.parseInt(values[0]);
        return values.length == 1 ? min : min+ RANDOM.nextInt(Integer.parseInt(values[1])-min+1);
    }
    public List<String> getRandomRewards(boolean canRepeatRewards) {
        final List<String> a = new ArrayList<>(), excluding = new ArrayList<>();
        final boolean opp = !canRepeatRewards;
        for(int i = 1; i <= getRandomRewardSize(); i++) {
            final String r = getRandomReward(excluding);
            a.add(r);
            if(opp) {
                excluding.add(r);
            }
        }
        return a;
    }
}
