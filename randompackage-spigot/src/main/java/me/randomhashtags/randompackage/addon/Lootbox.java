package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.addon.enums.LootboxRewardType;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface Lootbox extends Itemable, Nameable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "lootbox" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final Lootbox lootbox = getLootbox(originalInput.split(":")[1]);
        return lootbox != null ? lootbox.getItem() : null;
    }

    int getPriority();
    long getAvailableFor();
    int getGuiSize();
    String getGuiTitle();
    String getPreviewTitle();
    String getRegularLootSize();
    String getBonusLootSize();

    List<String> getGuiFormat();
    List<String> getRegularLootFormat();
    List<String> getJackpotLootFormat();
    List<String> getBonusLootFormat();
    List<String> getRegularLoot();
    List<String> getJackpotLoot();
    List<String> getBonusLoot();
    ItemStack getBackground();

    default List<String> getRewards(final LootboxRewardType type) {
        switch (type) {
            case REGULAR: return getRegularLoot();
            case JACKPOT: return getJackpotLoot();
            case BONUS: return getBonusLoot();
            default: return new ArrayList<>();
        }
    }
    default List<ItemStack> getAllRewards(final LootboxRewardType type) {
        final List<ItemStack> items = new ArrayList<>();
        final List<String> rewards = getRewards(type);
        final RandomPackageAPI api = RandomPackageAPI.INSTANCE;
        for(String string : rewards) {
            items.add(api.createItemStack(null, string));
        }
        return items;
    }
    default String getRandomLoot(final LootboxRewardType type, final Random random, final List<String> excluding) {
        final List<String> loot = new ArrayList<>(getRewards(type));
        for(String s : excluding) {
            loot.remove(s);
        }
        return loot.get(random.nextInt(loot.size()));
    }

    default List<ItemStack> getAllRewards() {
        final List<ItemStack> items = new ArrayList<>();
        final RandomPackageAPI api = RandomPackageAPI.INSTANCE;
        for(String string : getRegularLoot()) {
            items.add(api.createItemStack(null, string));
        }
        for(String string : getJackpotLoot()) {
            items.add(api.createItemStack(null, string));
        }
        for(String string : getBonusLoot()) {
            items.add(api.createItemStack(null, string));
        }
        return items;
    }
}
