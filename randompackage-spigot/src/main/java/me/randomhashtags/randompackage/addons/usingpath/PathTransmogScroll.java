package me.randomhashtags.randompackage.addons.usingpath;

import me.randomhashtags.randompackage.addons.TransmogScroll;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.randomhashtags.randompackage.utils.CustomEnchantUtils.config;

public class PathTransmogScroll extends TransmogScroll {
    private String path;
    private ItemStack item;
    public PathTransmogScroll(String path) {
        this.path = path;
        addTransmogScroll(getIdentifier(), this);
    }
    public String getIdentifier() { return path; }

    public ItemStack getItem() {
        if(item == null) item = api.d(config, "transmog scrolls." + path);
        return item;
    }
    public String getApplied() { return ChatColor.translateAlternateColorCodes('&', config.getString("transmog scrolls." + path + ".apply")); }
    public List<String> getRarityOrganization() { return config.getStringList("transmog scrolls." + path + ".rarity organization"); }
    public List<String> getAppliesTo() { return config.getStringList("transmog scrolls." + path + ".applies to"); }
}
