package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import me.randomhashtags.randompackage.utils.NamespacedKey;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public abstract class AbstractLootbox extends AbstractRPFeature {
    public static HashMap<NamespacedKey, AbstractLootbox> lootboxes;
    private static Random random = new Random();

    public void created(NamespacedKey key) {
        if(lootboxes == null) lootboxes = new HashMap<>();
        lootboxes.put(key, this);
    }

    public abstract NamespacedKey getNamespacedKey();
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

    public static AbstractLootbox valueOf(String guiTitle) {
        if(lootboxes != null) {
            for(AbstractLootbox l : lootboxes.values())
                if(l.getGuiTitle().equals(guiTitle))
                    return l;
        }
        return null;
    }
    public static AbstractLootbox valueof(String previewTitle) {
        if(lootboxes != null) {
            previewTitle = ChatColor.stripColor(previewTitle);
            for(AbstractLootbox l : lootboxes.values()) {
                if(ChatColor.stripColor(l.getPreviewTitle()).equals(previewTitle)) {
                    return l;
                }
            }
        }
        return null;
    }
    public static AbstractLootbox valueOf(ItemStack is) {
        if(lootboxes != null && is != null && is.hasItemMeta())
            for(AbstractLootbox l : lootboxes.values())
                if(l.getItem().isSimilar(is))
                    return l;
        return null;
    }
    public static AbstractLootbox valueOf(int priority) {
        if(lootboxes != null) {
            for(AbstractLootbox l : lootboxes.values())
                if(l.getPriority() == priority)
                    return l;
        }
        return null;
    }
    public static AbstractLootbox latest() {
        int p = 0;
        AbstractLootbox lo = null;
        if(lootboxes != null) {
            for(AbstractLootbox l : lootboxes.values()) {
                final int P = l.getPriority();
                if(lo == null || P > p) {
                    p = P;
                    lo = l;
                }
            }
        }
        return lo;
    }

    public static void deleteAll() {
        lootboxes = null;
        random = null;
    }
}
