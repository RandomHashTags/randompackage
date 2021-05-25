package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Lootbox;
import me.randomhashtags.randompackage.enums.LootboxRewardType;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileLootbox extends RPAddon implements Lootbox {
    private ItemStack item, background;

    public FileLootbox(File f) {
        load(f);
        register(Feature.LOOTBOX, this);
    }
    public String getIdentifier() { return getYamlName(); }

    public int getPriority() {
        return yml.getInt("priority");
    }
    public long getAvailableFor() {
        return yml.getLong("available for");
    }
    public int getGuiSize() {
        return yml.getInt("gui.size");
    }
    public String getName() {
        return getString(yml, "name");
    }
    public String getGuiTitle() {
        return getString(yml, "gui.title");
    }
    public String getPreviewTitle() {
        return getString(yml, "preview title");
    }
    public String getRegularLootSize() {
        return getString(yml, "regular loot size");
    }
    public String getBonusLootSize() {
        return getString(yml, "bonus loot size");
    }
    public List<String> getGuiFormat() {
        return getStringList(yml, "gui.format");
    }
    public List<String> getRegularLootFormat() {
        return getStringList(yml, "lore formats.regular loot");
    }
    public List<String> getJackpotLootFormat() {
        return getStringList(yml, "lore formats.jackpot loot");
    }
    public List<String> getBonusLootFormat() {
        return getStringList(yml, "lore formats.bonus loot");
    }
    public List<String> getRegularLoot() {
        return getStringList(yml, "regular loot");
    }
    public List<String> getJackpotLoot() {
        return getStringList(yml, "jackpot loot");
    }
    public List<String> getBonusLoot() {
        return getStringList(yml, "bonus loot");
    }

    public ItemStack getItem() {
        if(item == null) {
            final ItemStack i = createItemStack(yml, "lootbox");
            final ItemMeta itemMeta = i.getItemMeta();
            final List<String> lore = new ArrayList<>(), regularLootFormat = getRegularLootFormat(), jackpotLootFormat = getJackpotLootFormat(), bonusLootFormat = getBonusLootFormat();
            final List<ItemStack> regular = getAllRewards(LootboxRewardType.REGULAR);
            final List<ItemStack> jackpot = getAllRewards(LootboxRewardType.JACKPOT);
            final List<ItemStack> bonus = getAllRewards(LootboxRewardType.BONUS);
            for(String s : itemMeta.getLore()) {
                if(s.equals("{REGULAR_LOOT}")) {
                    for(ItemStack is : regular) {
                        if(is != null) {
                            final ItemMeta m = is.getItemMeta();
                            final String d = m != null ? m.getDisplayName() : null, t = is.getType().name();
                            for(String z : regularLootFormat) {
                                final String it = d != null ? d : toMaterial(t, false);
                                lore.add(z.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", it));
                            }
                        }
                    }
                } else if(s.equals("{JACKPOT_LOOT}")) {
                    for(ItemStack is : jackpot) {
                        if(is != null) {
                            final ItemMeta m = is.getItemMeta();
                            final String d = m != null ? m.getDisplayName() : null, t = is.getType().name();
                            for(String z : jackpotLootFormat) {
                                final String it = d != null ? d : toMaterial(t, false);
                                lore.add(z.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", it));
                            }
                        }
                    }
                } else if(s.equals("{BONUS_LOOT}")) {
                    for(ItemStack is : bonus) {
                        if(is != null) {
                            final ItemMeta m = is.getItemMeta();
                            final String d = m != null ? m.getDisplayName() : null, t = is.getType().name();
                            for(String z : bonusLootFormat) {
                                final String it = d != null ? d : toMaterial(t, false);
                                lore.add(z.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", it));
                            }
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
        return getClone(item);
    }
    public ItemStack getBackground() {
        if(background == null) background = createItemStack(yml, "gui.background");
        return getClone(background);
    }
}
