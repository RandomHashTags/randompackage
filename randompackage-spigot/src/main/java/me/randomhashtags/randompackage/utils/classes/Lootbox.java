package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.abstraction.AbstractLootbox;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class Lootbox extends AbstractLootbox {
    private List<String> regularLootFormat, jackpotLootFormat, bonusLootFormat;
    private ItemStack item, background;
    private NamespacedKey key;

    public Lootbox(File f) {
        load(f);
        created(getNamespacedKey());
    }

    public NamespacedKey getNamespacedKey() {
        if(key == null) key = new NamespacedKey(getPlugin, getYamlName());
        return key;
    }
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
}
