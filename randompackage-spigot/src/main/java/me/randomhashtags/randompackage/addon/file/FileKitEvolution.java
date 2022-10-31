package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomKitEvolution;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.api.addon.KitsEvolution;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class FileKitEvolution extends RPKitSpigot implements CustomKitEvolution {
    private ItemStack item, upgradeGem;

    public FileKitEvolution(File f) {
        load(f);
        register(Feature.CUSTOM_KIT, this);
    }
    public Kits getKitClass() {
        return KitsEvolution.getKitsEvolution();
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        if(item == null) item = createItemStack(yml, "gui settings");
        return getClone(item);
    }
    public int getUpgradeChance() {
        return yml.getInt("settings.upgrade chance");
    }
    public ItemStack getUpgradeGem() {
        if(upgradeGem == null) upgradeGem = createItemStack(yml, "upgrade gem");
        return getClone(upgradeGem);
    }
}
