package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.utils.RPAddon;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class MonthlyCrate extends RPAddon implements Itemable {
    public static HashMap<Player, List<String>> revealedRegular, revealedBonus;

    public abstract int getCategory();
    public abstract int getCategorySlot();
    public abstract String getGuiTitle();
    public abstract List<String> getRewards();
    public abstract List<String> getBonusRewards();
    public abstract List<String> getRedeemFormat();
    public abstract List<String> getBonusFormat();
    public abstract ItemStack getBackground();
    public abstract ItemStack getRedeem();
    public abstract ItemStack getBonus1();
    public abstract ItemStack getBonus2();
    public abstract UInventory getRegular();
    public abstract UInventory getBonus();
    public abstract List<Integer> getRewardSlots();
    public abstract List<Integer> getBonusRewardSlots();
    public abstract ItemStack getRandomReward(Player player, List<String> excluding, boolean canRepeatRewards);
    public abstract ItemStack getRandomBonusReward(Player player, List<String> excluding, boolean canRepeatRewards);

    public static MonthlyCrate valueOf(String title) {
        if(monthlycrates != null) {
            for(MonthlyCrate m : monthlycrates.values()) {
                if(m.getGuiTitle().equals(title)) {
                    return m;
                }
            }
        }
        return null;
    }
    public static MonthlyCrate valueOf(ItemStack item) {
        if(monthlycrates != null) {
            for(MonthlyCrate c : monthlycrates.values()) {
                if(c.getItem().isSimilar(item)) {
                    return c;
                }
            }
        }
        return null;
    }
    public static MonthlyCrate valueOf(Player player, ItemStack item) {
        if(monthlycrates != null && player != null && item != null) {
            final String p = player.getName();
            for(MonthlyCrate c : monthlycrates.values()) {
                final ItemStack is = c.getItem(), IS = is.clone();
                final ItemMeta m = is.getItemMeta();
                final List<String> s = new ArrayList<>();
                if(m.hasLore()) {
                    for(String l : m.getLore()) {
                        s.add(l.replace("{UNLOCKED_BY}", p));
                    }
                    m.setLore(s);
                }
                is.setItemMeta(m);
                if(item.isSimilar(is) || item.isSimilar(IS)) {
                    return c;
                }
            }
        }
        return null;
    }
    public static MonthlyCrate valueOf(int category, int slot) {
        if(monthlycrates != null) {
            for(MonthlyCrate c : monthlycrates.values()) {
                if(category == c.getCategory() && slot == c.getCategorySlot()) {
                    return c;
                }
            }
        }
        return null;
    }
}
