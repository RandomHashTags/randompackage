package me.randomhashtags.randompackage.addons.usingpath;

import me.randomhashtags.randompackage.addons.TransmogScroll;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PathTransmogScroll extends TransmogScroll {
    private String path;
    private ItemStack item;
    public PathTransmogScroll(String path) {
        this.path = path;
        addTransmogScroll(getIdentifier(), this);
    }
    public String getIdentifier() { return path; }

    public ItemStack getItem() {
        if(item == null) item = api.d(getAddonConfig("transmog scrolls.yml"), "transmog scrolls." + path);
        return item;
    }
    public String getApplied() { return ChatColor.translateAlternateColorCodes('&', getAddonConfig("transmog scrolls.yml").getString("transmog scrolls." + path + ".apply")); }
    public List<String> getRarityOrganization() { return getAddonConfig("transmog scrolls.yml").getStringList("transmog scrolls." + path + ".rarity organization"); }
    public List<String> getAppliesTo() { return getAddonConfig("transmog scrolls.yml").getStringList("transmog scrolls." + path + ".applies to"); }
}
