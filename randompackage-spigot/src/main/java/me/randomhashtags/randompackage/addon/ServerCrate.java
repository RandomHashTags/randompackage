package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.universal.UInventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.API;

public interface ServerCrate extends Itemable {
    int getRedeemableItems();
    String getDisplayRarity();
    List<Integer> getSelectableSlots();
    UInventory getInventory();
    List<String> getFormat();
    LinkedHashMap<String, Integer> getRevealChances();
    ItemStack getDisplay();
    ItemStack getOpenGui();
    ItemStack getSelected();
    ItemStack getRevealSlotRarity();
    HashMap<String, List<String>> getRewards();
    default List<ItemStack> getAllRewards() {
        final List<ItemStack> items = new ArrayList<>();
        final HashMap<String, List<String>> rewards = getRewards();
        for(String rarity : rewards.keySet()) {
            for(String item : rewards.get(rarity)) {
                final ItemStack target = API.createItemStack(null, item);
                if(target != null && !target.getType().equals(Material.AIR)) {
                    items.add(target);
                }
            }
        }
        return items;
    }
    ItemStack getBackground();
    ItemStack getBackground2();
    ServerCrateFlare getFlare();
    ServerCrate getRandomRarity(boolean useChances);
    ItemStack getRandomReward(String rarity);
}
