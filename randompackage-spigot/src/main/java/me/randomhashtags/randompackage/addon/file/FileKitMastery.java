package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.CustomKitMastery;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.api.addon.KitsMastery;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public final class FileKitMastery extends RPKitSpigot implements CustomKitMastery {
    private final MultilingualString name;
    private final ItemStack item, redeem, shard, antiCrystal;
    private final boolean loses_required_kits;
    private final LinkedHashMap<CustomKit, Integer> requiredKits;
    private final List<String> anti_crystal_negated_enchants;
    private final String anti_crystal_applied;
    public FileKitMastery(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        final JSONObject settings_json = parse_json_in_json(json, "settings");
        name = parse_multilingual_string_in_json(settings_json, "name");
        loses_required_kits = parse_boolean_in_json(settings_json, "loses required kits");

        requiredKits = new LinkedHashMap<>();
        final List<String> required = parse_list_string_in_json(json, "required kits");
        for(String s : required) {
            final String[] a = s.split(";");
            requiredKits.put(getCustomKit(a[0]), Integer.parseInt(a[1]));
        }

        item = set(create_item_stack(json, "gui settings"));
        redeem = set(create_item_stack(json, "redeem"));
        shard = set(create_item_stack(json, "shard"));
        antiCrystal = set(create_item_stack(json, "anti crystal"));
        final JSONObject anti_crystal_json = parse_json_in_json(json, "anti crystal");
        anti_crystal_negated_enchants = parse_list_string_in_json(anti_crystal_json, "negate enchants");
        anti_crystal_applied = parse_string_in_json(anti_crystal_json, "applied");

        register(Feature.CUSTOM_KIT, this);
    }
    public @NotNull Kits getKitClass() {
        return KitsMastery.getKitsMastery();
    }

    public @NotNull MultilingualString getName() {
        return name;
    }
    @NotNull
    @Override
    public ItemStack getItem() {
        return getClone(item);
    }
    @Override
    public ItemStack getRedeem() {
        return getClone(redeem);
    }
    private ItemStack set(ItemStack is) {
        if(is != null) {
            final ItemMeta m = is.getItemMeta();
            final List<String> l = new ArrayList<>();
            int req = 0;
            final HashMap<CustomKit, Integer> re = getRequiredKits();
            final CustomKit[] kitKeys = re.keySet().toArray(new CustomKit[re.size()]);
            for(String s : m.getLore()) {
                if(s.contains("{REQUIREMENT}")) {
                    final CustomKit kit = kitKeys[req];
                    final ItemStack kit_item = kit != null ? kit.getItem() : null;
                    final String name;
                    if(kit_item != null) {
                        name = kit.getItem().getItemMeta().getDisplayName();
                    } else {
                        name = "???";
                    }
                    s = s.replace("{REQUIREMENT}", name).replace("{TIER}", toRoman(re.get(kit)));
                    req++;
                }
                l.add(s);
                m.setLore(l);
                is.setItemMeta(m);
            }
        }
        return is;
    }
    @Override
    public LinkedHashMap<CustomKit, Integer> getRequiredKits() {
        return requiredKits;
    }
    @Override
    public boolean losesRequiredKits() {
        return loses_required_kits;
    }
    @Override
    public ItemStack getShard() {
        return getClone(shard);
    }
    @Override
    public ItemStack getAntiCrystal() {
        return getClone(antiCrystal);
    }
    @Override
    public List<String> getAntiCrystalNegatedEnchants() {
        return anti_crystal_negated_enchants;
    }
    @Override
    public String getAntiCrystalApplied() {
        return anti_crystal_applied;
    }
}
