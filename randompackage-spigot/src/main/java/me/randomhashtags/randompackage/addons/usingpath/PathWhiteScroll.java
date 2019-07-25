package me.randomhashtags.randompackage.addons.usingpath;

import me.randomhashtags.randompackage.addons.WhiteScroll;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.randomhashtags.randompackage.utils.CustomEnchantUtils.config;

public class PathWhiteScroll extends WhiteScroll {
    private String path, apply;
    private ItemStack item;
    public PathWhiteScroll(String path) {
        this.path = path;
        addWhiteScroll(getIdentifier(), this);
    }
    public String getIdentifier() { return path; }

    public String getRequiredWhiteScroll() { return config.getString("white scrolls." + path + ".required white scroll"); }
    public ItemStack getItem() {
        if(item == null) item = api.d(config, "white scrolls." + path);
        return item.clone();
    }
    public String getApplied() {
        if(apply == null) apply = ChatColor.translateAlternateColorCodes('&', config.getString("white scrolls." + path + ".apply"));
        return apply;
    }
    public List<String> getAppliesTo() { return config.getStringList("white scrolls." + path + ".applies to"); }
}
