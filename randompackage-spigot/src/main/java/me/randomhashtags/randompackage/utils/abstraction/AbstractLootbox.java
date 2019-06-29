package me.randomhashtags.randompackage.utils.abstraction;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public abstract class AbstractLootbox extends Saveable {
    private static Random random = new Random();

    private List<String> regularLootFormat, jackpotLootFormat, bonusLootFormat;
    private ItemStack item, background;

    public String getName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("name")); }
    public String getGuiTitle() { return ChatColor.translateAlternateColorCodes('&', yml.getString("gui.title")); }
    public String getPreviewTitle() { return ChatColor.translateAlternateColorCodes('&', yml.getString("preview title")); }
    public String getRegularLootSize() { return yml.getString("regular loot size"); }
    public String getBonusLootSize() { return yml.getString("bonus loot size"); }
    public int getPriority() { return yml.getInt("priority"); }
    public int getAvailableFor() { return yml.getInt("available for"); }
    public int getGuiSize() { return yml.getInt("gui.size"); }
    public List<String> getGuiFormat() { return yml.getStringList("gui.format"); }
    public List<String> getRegularLootFormat() {
        if(regularLootFormat == null) regularLootFormat = api.colorizeListString(yml.getStringList("lore formats.regular loot"));
        return regularLootFormat;
    }
    public List<String> getJackpotLootFormat() {
        if(jackpotLootFormat == null) jackpotLootFormat = api.colorizeListString(yml.getStringList("lore formats.jackpot loot"));
        return jackpotLootFormat;
    }
    public List<String> getBonusLootFormat() {
        if(bonusLootFormat == null) bonusLootFormat = api.colorizeListString(yml.getStringList("lore formats.bonus loot"));
        return bonusLootFormat;
    }
    public List<String> getRandomLoot() { return yml.getStringList("regular loot"); }
    public List<String> getJackpotLoot() { return yml.getStringList("jackpot loot"); }
    public List<String> getBonusLoot() { return yml.getStringList("bonus loot"); }

    public ItemStack getItem() {
        if(item == null) {
            final ItemStack i = api.d(yml, "lootbox");
            final ItemMeta itemMeta = i.getItemMeta();
            final List<String> lore = new ArrayList<>(), regularLootFormat = getRegularLootFormat(), jackpotLootFormat = getJackpotLootFormat(), bonusLootFormat = getBonusLootFormat();
            for(String s : itemMeta.getLore()) {
                if(s.equals("{REGULAR_LOOT}")) {
                    for(ItemStack is : regularLoot()) {
                        final ItemMeta m = is.getItemMeta();
                        final String d = m != null ? m.getDisplayName() : null, t = is.getType().name();
                        for(String z : regularLootFormat) {
                            final String it = d != null ? d : api.toMaterial(t, false);
                            lore.add(z.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", it));
                        }
                    }
                } else if(s.equals("{JACKPOT_LOOT}")) {
                    for(ItemStack is : jackpotLoot()) {
                        final ItemMeta m = is.getItemMeta();
                        final String d = m != null ? m.getDisplayName() : null, t = is.getType().name();
                        for(String z : jackpotLootFormat) {
                            final String it = d != null ? d : api.toMaterial(t, false);
                            lore.add(z.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", it));
                        }
                    }
                } else if(s.equals("{BONUS_LOOT}")) {
                    for(ItemStack is : bonusLoot()) {
                        final ItemMeta m = is.getItemMeta();
                        final String d = m != null ? m.getDisplayName() : null, t = is.getType().name();
                        for(String z : bonusLootFormat) {
                            final String it = d != null ? d : api.toMaterial(t, false);
                            lore.add(z.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", it));
                        }
                    }
                } else {
                    lore.add(s);
                }
            }
            itemMeta.setLore(lore);
            i.setItemMeta(itemMeta);
            this.item = i;
        }
        return item.clone();
    }
    public ItemStack getBackground() {
        if(background == null) background = api.d(yml, "gui.background");
        return background.clone();
    }
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
