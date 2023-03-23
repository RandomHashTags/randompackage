package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.WhiteScroll;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PathWhiteScroll extends RPAddonSpigot implements WhiteScroll {
    private final String path;
    private String apply;
    private ItemStack item;
    public PathWhiteScroll(String path) {
        this.path = path;
        register(Feature.SCROLL_WHITE, this);
    }
    @NotNull
    @Override
    public String getIdentifier() {
        return path;
    }

    public boolean canBeApplied(@NotNull ItemStack is) {
        if(!is.getType().name().contains("AIR")) {
            final List<WhiteScroll> list = valueOfWhiteScrollApplied(is);
            final String reqws = getRequiredWhiteScroll();
            if(list == null && reqws == null || list != null && !list.contains(this) && (reqws == null || list.contains(getWhiteScroll(reqws)))) {
                final String material = is.getType().name();
                for(String s : getAppliesTo()) {
                    if(material.endsWith(s.toUpperCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public String getRequiredWhiteScroll() {
        return getString(getAddonConfig("scrolls.yml"), "white scrolls." + path + ".required white scroll");
    }
    public boolean removesRequiredAfterApplication() {
        return getAddonConfig("scrolls.yml").getBoolean("white scrolls." + path + ".removes required after application");
    }
    public @NotNull ItemStack getItem() {
        if(item == null) item = createItemStack(getAddonConfig("scrolls.yml"), "white scrolls." + path);
        return getClone(item);
    }
    public String getApplied() {
        if(apply == null) apply = colorize(getAddonConfig("scrolls.yml").getString("white scrolls." + path + ".apply"));
        return apply;
    }
    public List<String> getAppliesTo() {
        return getStringList(getAddonConfig("scrolls.yml"), "white scrolls." + path + ".applies to");
    }
}
