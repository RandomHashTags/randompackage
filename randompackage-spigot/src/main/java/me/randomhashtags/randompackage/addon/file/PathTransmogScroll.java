package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.TransmogScroll;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PathTransmogScroll extends RPAddonSpigot implements TransmogScroll {
    private final String path;
    private ItemStack item;
    public PathTransmogScroll(String path) {
        this.path = path;
        register(Feature.SCROLL_TRANSMOG, this);
    }
    @NotNull
    @Override
    public String getIdentifier() {
        return path;
    }

    public boolean canBeApplied(@NotNull ItemStack is) {
        final String material = is.getType().name();
        for(String s : getAppliesTo()) {
            if(material.endsWith(s.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    public @NotNull ItemStack getItem() {
        if(item == null) item = createItemStack(getAddonConfig("scrolls.yml"), "transmog scrolls." + path);
        return getClone(item);
    }
    public String getApplied() {
        return getString(getAddonConfig("scrolls.yml"), "transmog scrolls." + path + ".apply");
    }
    public List<String> getRarityOrganization() {
        return getStringList(getAddonConfig("scrolls.yml"), "transmog scrolls." + path + ".rarity organization");
    }
    public List<String> getAppliesTo() {
        return getStringList(getAddonConfig("scrolls.yml"), "transmog scrolls." + path + ".applies to");
    }
}
