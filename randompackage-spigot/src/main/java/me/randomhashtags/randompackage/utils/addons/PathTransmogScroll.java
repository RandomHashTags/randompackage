package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.TransmogScroll;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PathTransmogScroll extends RPAddon implements TransmogScroll {
    private String path;
    private ItemStack item;
    public PathTransmogScroll(String path) {
        this.path = path;
        addTransmogScroll(this);
    }
    public String getIdentifier() { return path; }

    public boolean canBeApplied(ItemStack is) {
        if(is != null) {
            final String m = is.getType().name();
            for(String s : getAppliesTo()) {
                if(m.endsWith(s.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }
    public ItemStack getItem() {
        if(item == null) item = api.d(getAddonConfig("transmog scrolls.yml"), "transmog scrolls." + path);
        return item;
    }
    public String getApplied() { return ChatColor.translateAlternateColorCodes('&', getAddonConfig("transmog scrolls.yml").getString("transmog scrolls." + path + ".apply")); }
    public List<String> getRarityOrganization() { return getAddonConfig("transmog scrolls.yml").getStringList("transmog scrolls." + path + ".rarity organization"); }
    public List<String> getAppliesTo() { return getAddonConfig("transmog scrolls.yml").getStringList("transmog scrolls." + path + ".applies to"); }
}
