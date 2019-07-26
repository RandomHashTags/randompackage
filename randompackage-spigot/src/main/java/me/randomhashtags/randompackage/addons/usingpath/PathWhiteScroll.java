package me.randomhashtags.randompackage.addons.usingpath;

import me.randomhashtags.randompackage.addons.WhiteScroll;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PathWhiteScroll extends WhiteScroll {
    private String path, apply;
    private ItemStack item;
    public PathWhiteScroll(String path) {
        this.path = path;
        addWhiteScroll(getIdentifier(), this);
    }
    public String getIdentifier() { return path; }

    public String getRequiredWhiteScroll() { return getAddonConfig("white scrolls.yml").getString("white scrolls." + path + ".required white scroll"); }
    public boolean removesRequiredAfterApplication() { return getAddonConfig("white scrolls.yml").getBoolean("white scrolls." + path + ".removes required after application"); }
    public ItemStack getItem() {
        if(item == null) item = api.d(getAddonConfig("white scrolls.yml"), "white scrolls." + path);
        return item.clone();
    }
    public String getApplied() {
        if(apply == null) apply = ChatColor.translateAlternateColorCodes('&', getAddonConfig("white scrolls.yml").getString("white scrolls." + path + ".apply"));
        return apply;
    }
    public List<String> getAppliesTo() { return getAddonConfig("white scrolls.yml").getStringList("white scrolls." + path + ".applies to"); }
}
