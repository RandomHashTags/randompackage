package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.utils.RPAddon;
import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface Lootbox extends Itemable {
    String getName();
    String getGuiTitle();
    String getPreviewTitle();
    String getRegularLootSize();
    String getBonusLootSize();
    int getPriority();
    int getAvailableFor();
    int getGuiSize();
    List<String> getGuiFormat();
    List<String> getRegularLootFormat();
    List<String> getJackpotLootFormat();
    List<String> getBonusLootFormat();
    List<String> getRandomLoot();
    List<String> getJackpotLoot();
    List<String> getBonusLoot();
    ItemStack getBackground();

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
