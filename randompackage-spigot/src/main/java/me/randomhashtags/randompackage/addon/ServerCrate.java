package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.universal.UInventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface ServerCrate extends Itemable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "serverflare", "spaceflare", "servercrateflare", "servercrate", "spacecrate", "spacechest", "serverchest" };
    }
    @Nullable
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final boolean isFlare = lowercaseInput.contains("flare");
        final ServerCrate crate = getServerCrate(originalInput.split(":")[1]);
        return crate != null ? isFlare ? crate.getFlare().getItem() : crate.getItem() : null;
    }

    int getRedeemableItems();
    String getDisplayRarity();
    @NotNull List<Integer> getSelectableSlots();
    UInventory getInventory();
    @NotNull List<String> getFormat();
    @NotNull LinkedHashMap<String, Integer> getRevealChances();
    @NotNull ItemStack getDisplay();
    @NotNull ItemStack getOpenGui();
    @NotNull ItemStack getSelected();
    @NotNull ItemStack getRevealSlotRarity();
    @NotNull HashMap<String, List<String>> getRewards();
    @NotNull
    default List<ItemStack> getAllRewards() {
        final List<ItemStack> items = new ArrayList<>();
        final HashMap<String, List<String>> rewards = getRewards();
        final RandomPackageAPI api = RandomPackageAPI.INSTANCE;
        for(Map.Entry<String, List<String>> entry : rewards.entrySet()) {
            for(String item : entry.getValue()) {
                final ItemStack target = api.createItemStack(null, item);
                if(target != null && !target.getType().equals(Material.AIR)) {
                    items.add(target);
                }
            }
        }
        return items;
    }
    @NotNull ItemStack getBackground();
    @NotNull ItemStack getBackground2();
    @NotNull ServerCrateFlare getFlare();
    ServerCrate getRandomRarity(boolean useChances);
    ItemStack getRandomReward(String rarity);
}
