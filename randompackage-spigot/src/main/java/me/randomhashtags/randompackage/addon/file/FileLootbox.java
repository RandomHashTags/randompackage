package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Lootbox;
import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.addon.enums.LootboxRewardType;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileLootbox extends RPAddonSpigot implements Lootbox {
    private final int priority;
    private final long availability_duration;
    private final int gui_size;
    private final MultilingualString name;
    private final String gui_title, preview_title, regular_loot_size, bonus_loot_size;
    private final List<String> gui_format;
    private final List<String> lore_format_regular, lore_format_jackpot, lore_format_bonus;
    private final List<String> regular_loot, jackpot_loot, bonus_loot;

    private final ItemStack item, background;

    public FileLootbox(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        priority = parse_int_in_json(json, "priority");
        availability_duration = parse_long_in_json(json, "available for");
        name = parse_multilingual_string_in_json(json, "name");

        final JSONObject gui_json = parse_json_in_json(json, "gui");
        gui_size = parse_int_in_json(gui_json, "size");
        gui_title = parse_string_in_json(gui_json, "title");
        preview_title = parse_string_in_json(json, "preview title");
        regular_loot_size = parse_string_in_json(json, "regular loot size");
        bonus_loot_size = parse_string_in_json(json, "bonus loot size");
        gui_format = parse_list_string_in_json(gui_json, "format");

        final JSONObject lore_format_json = parse_json_in_json(json, "lore formats");
        lore_format_regular = parse_list_string_in_json(lore_format_json, "regular loot");
        lore_format_jackpot = parse_list_string_in_json(lore_format_json, "jackpot loot");
        lore_format_bonus = parse_list_string_in_json(lore_format_json, "bonus loot");

        regular_loot = parse_list_string_in_json(json, "regular loot");
        jackpot_loot = parse_list_string_in_json(json, "jackpot loot");
        bonus_loot = parse_list_string_in_json(json, "bonus loot");

        item = create_item_stack(json, "lootbox");
        final ItemMeta itemMeta = item.getItemMeta();
        final List<String> lore = new ArrayList<>(), regularLootFormat = getRegularLootFormat(), jackpotLootFormat = getJackpotLootFormat(), bonusLootFormat = getBonusLootFormat();
        final List<ItemStack> regular = getAllRewards(LootboxRewardType.REGULAR);
        final List<ItemStack> jackpot = getAllRewards(LootboxRewardType.JACKPOT);
        final List<ItemStack> bonus = getAllRewards(LootboxRewardType.BONUS);
        for(String s : itemMeta.getLore()) {
            switch (s) {
                case "{REGULAR_LOOT}":
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
                    break;
                case "{JACKPOT_LOOT}":
                    for(ItemStack jackpot_item : jackpot) {
                        if(jackpot_item != null) {
                            final ItemMeta m = jackpot_item.getItemMeta();
                            final String d = m != null ? m.getDisplayName() : null, t = jackpot_item.getType().name();
                            for(String z : jackpotLootFormat) {
                                final String it = d != null ? d : toMaterial(t, false);
                                lore.add(z.replace("{AMOUNT}", Integer.toString(jackpot_item.getAmount())).replace("{ITEM}", it));
                            }
                        }
                    }
                    break;
                case "{BONUS_LOOT}":
                    for(ItemStack bonus_item : bonus) {
                        if(bonus_item != null) {
                            final ItemMeta m = bonus_item.getItemMeta();
                            final String d = m != null ? m.getDisplayName() : null, t = bonus_item.getType().name();
                            for(String z : bonusLootFormat) {
                                final String it = d != null ? d : toMaterial(t, false);
                                lore.add(z.replace("{AMOUNT}", Integer.toString(bonus_item.getAmount())).replace("{ITEM}", it));
                            }
                        }
                    }
                    break;
                default:
                    lore.add(s);
                    break;
            }
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        background = create_item_stack(gui_json, "background");
        register(Feature.LOOTBOX, this);
    }
    public int getPriority() {
        return priority;
    }
    public long getAvailableFor() {
        return availability_duration;
    }
    public int getGuiSize() {
        return gui_size;
    }
    public @NotNull MultilingualString getName() {
        return name;
    }
    public String getGuiTitle() {
        return gui_title;
    }
    public String getPreviewTitle() {
        return preview_title;
    }
    public String getRegularLootSize() {
        return regular_loot_size;
    }
    public String getBonusLootSize() {
        return bonus_loot_size;
    }
    public List<String> getGuiFormat() {
        return gui_format;
    }
    public List<String> getRegularLootFormat() {
        return lore_format_regular;
    }
    public List<String> getJackpotLootFormat() {
        return lore_format_jackpot;
    }
    public List<String> getBonusLootFormat() {
        return lore_format_bonus;
    }
    public List<String> getRegularLoot() {
        return regular_loot;
    }
    public List<String> getJackpotLoot() {
        return jackpot_loot;
    }
    public List<String> getBonusLoot() {
        return bonus_loot;
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        return getClone(item);
    }
    public ItemStack getBackground() {
        return getClone(background);
    }
}
