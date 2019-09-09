package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.Lootbox;
import me.randomhashtags.randompackage.addon.enums.LootboxRewardType;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileLootbox extends RPAddon implements Lootbox {
    private List<String> regularLootFormat, jackpotLootFormat, bonusLootFormat;
    private ItemStack item, background;

    public FileLootbox(File f) {
        load(f);
        addLootbox(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public int getPriority() { return yml.getInt("priority"); }
    public long getAvailableFor() { return yml.getLong("available for"); }
    public int getGuiSize() { return yml.getInt("gui.size"); }
    public String getName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("name")); }
    public String getGuiTitle() { return ChatColor.translateAlternateColorCodes('&', yml.getString("gui.title")); }
    public String getPreviewTitle() { return ChatColor.translateAlternateColorCodes('&', yml.getString("preview title")); }
    public String getRegularLootSize() { return yml.getString("regular loot size"); }
    public String getBonusLootSize() { return yml.getString("bonus loot size"); }
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
    public List<String> getRegularLoot() { return yml.getStringList("regular loot"); }
    public List<String> getJackpotLoot() { return yml.getStringList("jackpot loot"); }
    public List<String> getBonusLoot() { return yml.getStringList("bonus loot"); }

    public ItemStack getItem() {
        if(item == null) {
            final ItemStack i = api.d(yml, "lootbox");
            final ItemMeta itemMeta = i.getItemMeta();
            final List<String> lore = new ArrayList<>(), regularLootFormat = getRegularLootFormat(), jackpotLootFormat = getJackpotLootFormat(), bonusLootFormat = getBonusLootFormat();
            final List<ItemStack> regular = getAllRewards(LootboxRewardType.REGULAR);
            final List<ItemStack> jackpot = getAllRewards(LootboxRewardType.JACKPOT);
            final List<ItemStack> bonus = getAllRewards(LootboxRewardType.BONUS);
            for(String s : itemMeta.getLore()) {
                if(s.equals("{REGULAR_LOOT}")) {
                    for(ItemStack is : regular) {
                        final ItemMeta m = is.getItemMeta();
                        final String d = m != null ? m.getDisplayName() : null, t = is.getType().name();
                        for(String z : regularLootFormat) {
                            final String it = d != null ? d : api.toMaterial(t, false);
                            lore.add(z.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", it));
                        }
                    }
                } else if(s.equals("{JACKPOT_LOOT}")) {
                    for(ItemStack is : jackpot) {
                        final ItemMeta m = is.getItemMeta();
                        final String d = m != null ? m.getDisplayName() : null, t = is.getType().name();
                        for(String z : jackpotLootFormat) {
                            final String it = d != null ? d : api.toMaterial(t, false);
                            lore.add(z.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", it));
                        }
                    }
                } else if(s.equals("{BONUS_LOOT}")) {
                    for(ItemStack is : bonus) {
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
