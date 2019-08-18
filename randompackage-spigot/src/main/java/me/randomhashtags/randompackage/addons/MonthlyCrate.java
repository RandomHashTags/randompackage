package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public interface MonthlyCrate extends Itemable {
    HashMap<Player, List<String>> revealedRegular = new HashMap<>(), revealedBonus = new HashMap<>();
    int getCategory();
    int getCategorySlot();
    String getGuiTitle();
    List<String> getRewards();
    List<String> getBonusRewards();
    ItemStack getBackground();
    ItemStack getRedeem();
    ItemStack getBonus1();
    ItemStack getBonus2();
    UInventory getRegular();
    UInventory getBonus();
    List<Integer> getRewardSlots();
    List<Integer> getBonusRewardSlots();
}
