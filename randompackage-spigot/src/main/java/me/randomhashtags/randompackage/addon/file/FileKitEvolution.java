package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomKitEvolution;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.api.addon.KitsEvolution;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;

public final class FileKitEvolution extends RPKitSpigot implements CustomKitEvolution {
    private final ItemStack item, upgradeGem;
    private final int upgrade_chance;

    public FileKitEvolution(File f) {
        super(f);

        final JSONObject json = parse_json_from_file(f);
        item = create_item_stack(json, "gui settings");
        upgradeGem = create_item_stack(json, "upgrade gem");

        final JSONObject settings_json = json.getJSONObject("settings");
        upgrade_chance = parse_int_in_json(settings_json, "upgrade chance");

        register(Feature.CUSTOM_KIT, this);
    }
    public @NotNull Kits getKitClass() {
        return KitsEvolution.getKitsEvolution();
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        return getClone(item);
    }
    public int getUpgradeChance() {
        return upgrade_chance;
    }
    @Override
    public ItemStack getUpgradeGem() {
        return getClone(upgradeGem);
    }
}
