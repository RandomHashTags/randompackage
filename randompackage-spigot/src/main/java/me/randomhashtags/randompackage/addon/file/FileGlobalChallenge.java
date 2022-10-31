package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.GlobalChallenge;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public final class FileGlobalChallenge extends RPAddonSpigot implements GlobalChallenge {
    private ItemStack display;

    public FileGlobalChallenge(File f) {
        load(f);
        if(isEnabled()) {
            register(Feature.GLOBAL_CHALLENGE, this);
        }
    }

    @Override
    public boolean isEnabled() {
        return yml.getBoolean("settings.enabled", false);
    }
    @NotNull
    @Override
    public ItemStack getItem() {
        if(display == null) display = createItemStack(yml, "item");
        return getClone(display);
    }
    @Override
    public long getDuration() {
        return yml.getLong("settings.duration");
    }
    @Override
    public String getType() {
        return yml.getString("settings.type");
    }
    @Override
    public List<String> getAttributes() {
        return yml.getStringList("attributes");
    }
}
