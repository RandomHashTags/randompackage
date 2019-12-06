package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.WhiteScroll;
import me.randomhashtags.randompackage.api.addon.WhiteScrolls;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PathWhiteScroll extends RPAddon implements WhiteScroll {
    private String path, apply;
    private ItemStack item;
    public PathWhiteScroll(String path) {
        this.path = path;
        register(Feature.WHITE_SCROLL, this);
    }
    public String getIdentifier() { return path; }

    public boolean canBeApplied(ItemStack is) {
        if(is != null && !is.getType().name().contains("AIR")) {
            final List<WhiteScroll> a = WhiteScrolls.getWhiteScrolls().valueOfApplied(is);
            final String reqws = getRequiredWhiteScroll();
            if(a == null && reqws == null || a != null && !a.contains(this) && (reqws == null || a.contains(getWhiteScroll(reqws)))) {
                final String m = is.getType().name();
                for(String s : getAppliesTo()) {
                    if(m.endsWith(s.toUpperCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public String getRequiredWhiteScroll() { return getAddonConfig("white scrolls.yml").getString("white scrolls." + path + ".required white scroll"); }
    public boolean removesRequiredAfterApplication() { return getAddonConfig("white scrolls.yml").getBoolean("white scrolls." + path + ".removes required after application"); }
    public ItemStack getItem() {
        if(item == null) item = api.d(getAddonConfig("white scrolls.yml"), "white scrolls." + path);
        return getClone(item);
    }
    public String getApplied() {
        if(apply == null) apply = colorize(getAddonConfig("white scrolls.yml").getString("white scrolls." + path + ".apply"));
        return apply;
    }
    public List<String> getAppliesTo() { return getAddonConfig("white scrolls.yml").getStringList("white scrolls." + path + ".applies to"); }
}
