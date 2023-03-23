package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.ItemSkin;
import me.randomhashtags.randompackage.enums.Feature;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public final class FileItemSkin extends RPAddonSpigot implements ItemSkin {

    public FileItemSkin(File f) {
        super(f);
        register(Feature.ITEM_SKIN, this);
    }
    public @NotNull String getName() {
        return getString(yml, "name");
    }
    public String getMaterial() {
        return getString(yml, "material").toUpperCase();
    }
    public List<String> getLore() {
        return getStringList(yml, "lore");
    }
    public List<String> getAttributes() {
        return getStringList(yml, "attributes");
    }
}
