package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.enums.LootboxRewardType;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public interface Lootbox extends Itemable, Nameable {
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
        return type.equals(LootboxRewardType.REGULAR) ? getRegularLoot() : type.equals(LootboxRewardType.JACKPOT) ? getJackpotLoot() : getBonusLoot();
    }
    default List<ItemStack> getAllRewards(final LootboxRewardType type) {
        final List<ItemStack> items = new ArrayList<>();
        final List<String> l = getRewards(type);
        for(String s : l) {
            items.add(api.d(null, s));
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
        for(String s : getRegularLoot()) items.add(api.d(null, s));
        for(String s : getJackpotLoot()) items.add(api.d(null, s));
        for(String s : getBonusLoot()) items.add(api.d(null, s));
        return items;
    }
}
