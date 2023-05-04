package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileEnchantRarity extends RPAddonSpigot implements EnchantRarity {
    private final File folder;
    private final String[] revealed_enchant_rarities;
    private final List<String> revealed_enchant_message;
    private final ItemStack revealItem, revealedItem;
    private final String name_colors, apply_colors;
    private final boolean percents_add_up_to_100;
    private final String success, destroy;
    private final List<String> lore_format;
    private final Firework firework;
    protected List<CustomEnchantSpigot> enchants;

    public FileEnchantRarity(File folder, File f) {
        super(f);
        this.folder = folder;
        enchants = new ArrayList<>();

        final JSONObject json = parse_json_from_file(f);
        revealed_enchant_rarities = parse_string_in_json(json, "reveals enchant rarities").split(";");
        revealed_enchant_message = parse_list_string_in_json(json, "reveal enchant msg");
        revealItem = create_item_stack(json, "reveal item");
        revealedItem = create_item_stack(json, "revealed item");
        final JSONObject revealed_item_json = json.getJSONObject("revealed item");
        name_colors = parse_string_in_json(revealed_item_json, "name colors");
        apply_colors = parse_string_in_json(revealed_item_json, "apply colors");

        final JSONObject settings_json = json.optJSONObject("settings", new JSONObject());
        percents_add_up_to_100 = parse_boolean_in_json(settings_json, "success+destroy=100");
        success = parse_string_in_json(settings_json, "success");
        destroy = parse_string_in_json(settings_json, "destroy");
        lore_format = parse_list_string_in_json(settings_json, "lore format");

        final String[] values = parse_string_in_json(revealed_item_json, "firework").split(":");
        firework = createFirework(FireworkEffect.Type.valueOf(values[0].toUpperCase()), getColor(values[1]), getColor(values[2]), Integer.parseInt(values[3]));

        register(Feature.CUSTOM_ENCHANT_RARITY, this);
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return folder.getName();
    }

    public String[] getRevealedEnchantRarities() {
        return revealed_enchant_rarities;
    }
    public List<String> getRevealedEnchantMsg() {
        return revealed_enchant_message;
    }
    public ItemStack getRevealItem() {
        return getClone(revealItem);
    }
    @NotNull
    public ItemStack getRevealedItem() {
        return getClone(revealedItem);
    }
    public String getNameColors() {
        return name_colors;
    }
    public String getApplyColors() {
        return apply_colors;
    }
    public boolean percentsAddUpto100() {
        return percents_add_up_to_100;
    }
    public String getSuccess() {
        return success;
    }
    public String getDestroy() {
        return destroy;
    }
    public List<String> getLoreFormat() {
        return lore_format;
    }
    public int getSuccessSlot() {
        return getLoreFormat().indexOf("{SUCCESS}");
    }
    public int getDestroySlot() {
        return getLoreFormat().indexOf("{DESTROY}");
    }
    public Firework getFirework() {
        return firework;
    }
    public List<CustomEnchantSpigot> getEnchants() {
        return enchants;
    }
}
