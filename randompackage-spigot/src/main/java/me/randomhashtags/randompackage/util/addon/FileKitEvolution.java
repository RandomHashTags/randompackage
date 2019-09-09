package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.CustomKitEvolution;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.api.addon.KitsEvolution;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class FileKitEvolution extends RPKit implements CustomKitEvolution {
    private ItemStack item, upgradeGem;

    public FileKitEvolution(File f) {
        load(f);
        addKit(this);
    }
    public String getIdentifier() { return getYamlName(); }
    public Kits getKitClass() { return KitsEvolution.getKitsEvolution(); }

    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "gui settings");
        return item.clone();
    }
    public int getUpgradeChance() { return yml.getInt("settings.upgrade chance"); }
    public ItemStack getUpgradeGem() {
        if(upgradeGem == null) upgradeGem = api.d(yml, "upgrade gem");
        return upgradeGem != null ? upgradeGem.clone() : null;
    }
}
