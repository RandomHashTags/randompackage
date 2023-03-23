package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.addon.GlobalChallengePrize;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class GlobalChallengePrizeObject extends RPAddonSpigot implements GlobalChallengePrize {
    private final ItemStack display;
    private final int placement, amount;
    private final List<String> rewards;
    public GlobalChallengePrizeObject(ItemStack display, int amount, int placement, List<String> rewards) {
        super(null);
        this.display = display;
        this.amount = amount;
        this.placement = placement;
        this.rewards = rewards;
        register(Feature.GLOBAL_CHALLENGE_PRIZE, this);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "GLOBAL_CHALLENGE_PRIZE_" + placement;
    }

    public @NotNull ItemStack getItem() {
        return display;
    }
    public int getAmount() {
        return amount;
    }
    public int getPlacement() {
        return placement;
    }
    public List<String> getRewards() {
        return rewards;
    }
    public LinkedHashMap<String, ItemStack> getRandomRewards() {
        final LinkedHashMap<String, ItemStack> rewards = new LinkedHashMap<>();
        final List<String> availableRewards = new ArrayList<>(this.rewards);
        int amount = 0;
        for(int i = 0; i < availableRewards.size(); i++) {
            final String s = availableRewards.get(i);
            if(!s.toLowerCase().startsWith("chance=")) {
                rewards.put(s, createItemStack(null, s));
                availableRewards.remove(s);
                amount += 1;
                i -= 1;
            }
        }
        for(int i = amount; i < this.amount; i++) {
            if(availableRewards.isEmpty()) {
                return rewards;
            }
            final String randomReward = availableRewards.get(RANDOM.nextInt(availableRewards.size()));
            final int chance = getRemainingInt(randomReward.split(";")[0]);
            if(randomReward.toLowerCase().startsWith("chance=") && RANDOM.nextInt(100) <= chance) {
                final String target = randomReward.split("chance=" + chance + ";")[1];
                if(target.contains("||")) {
                    final String[] t = target.split("\\|\\|");
                    final String ta = t[RANDOM.nextInt(t.length)];
                    rewards.put(ta, createItemStack(null, ta));
                } else {
                    rewards.put(target, createItemStack(null, target));
                }
                availableRewards.remove(randomReward);
            } else {
                i -= 1;
            }
        }
        return rewards;
    }
}
