package me.randomhashtags.randompackage.recode.utils;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLootbox extends AbstractRPFeature {
    public abstract String getName();
    public abstract String getGuiTitle();
    public abstract String getPreviewTitle();
    public abstract String getRegularLootSize();
    public abstract String getBonusLootSize();
    public abstract int getPriority();
    public abstract int getAvailableFor();
    public abstract int getGuiSize();
    public abstract List<String> getGuiFormat();
    public abstract List<String> getRegularLootFormat();
    public abstract List<String> getJackpotLootFormat();
    public abstract List<String> getBonusLootFormat();
    public abstract List<String> getRandomLoot();
    public abstract List<String> getJackpotLoot();
    public abstract List<String> getBonusLoot();
    public abstract ItemStack getItem();
    public abstract ItemStack getBackground();

    public int randomRegularLootSize() {
        final String s = getRegularLootSize();
        final boolean b = s.contains("-");
        final int min = Integer.parseInt(b ? s.split("-")[0] : s), max = maxRegularLoot();
        return b ? min+random.nextInt(max-min+1) : min;
    }
    public int maxRegularLoot() {
        final String s = getRegularLootSize();
        return s.contains("-") ? Integer.parseInt(s.split("-")[1]) : Integer.parseInt(s);
    }
    public List<ItemStack> regularLoot() {
        final List<ItemStack> items = new ArrayList<>();
        for(String s : getRandomLoot()) {
            items.add(api.d(null, s));
        }
        return items;
    }
    public String randomRegularLoot(List<String> excluding) {
        final List<String> loot = new ArrayList<>(getRandomLoot());
        loot.addAll(getJackpotLoot());
        for(String s : excluding) {
            loot.remove(s);
        }
        return loot.get(random.nextInt(loot.size()));
    }
    public List<ItemStack> jackpotLoot() {
        final List<ItemStack> items = new ArrayList<>();
        for(String s : getJackpotLoot()) {
            items.add(api.d(null, s));
        }
        return items;
    }
    public String randomBonusLoot(List<String> excluding) {
        final List<String> loot = new ArrayList<>(getBonusLoot());
        for(String s : excluding) {
            loot.remove(s);
        }
        final int s = loot.size();
        return s > 0 ? loot.get(random.nextInt(s)) : "air";
    }
    public List<ItemStack> bonusLoot() {
        final List<ItemStack> items = new ArrayList<>();
        for(String s : getBonusLoot()) {
            items.add(api.d(null, s));
        }
        return items;
    }
    public List<ItemStack> items() {
        final List<ItemStack> items = new ArrayList<>();
        for(String s : getRandomLoot()) items.add(api.d(null, s));
        for(String s : getJackpotLoot()) items.add(api.d(null, s));
        for(String s : getBonusLoot()) items.add(api.d(null, s));
        return items;
    }
}
