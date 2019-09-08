package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.RarityGem;
import me.randomhashtags.randompackage.addons.SoulTracker;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PathSoulTracker extends RPAddon implements SoulTracker {
    private String path;
    private ItemStack is;
    private List<String> apply, split;
    public PathSoulTracker(String path) {
        this.path = path;
        addSoulTracker(this);
    }
    public String getIdentifier() { return path; }

    public boolean canBeApplied(ItemStack is) {
        if(is != null) {
            final String m = is.getType().name();
            for(String s : getAppliesTo()) {
                if(m.toUpperCase().endsWith(s)) {
                    return true;
                }
            }
        }
        return false;
    }
    public String getTracks() { return getAddonConfig("soul trackers.yml").getString("soul trackers." + path + ".tracks"); }
    public List<String> getAppliesTo() {
        return getAddonConfig("soul trackers.yml").getStringList("soul trackers." + path + ".applies to");
    }
    public String getSoulsPerKill() { return getAddonConfig("soul trackers.yml").getString("soul trackers." + path + ".souls per kill"); }
    public double getSoulsCollected() {
        double min = 0;
        final String p = getSoulsPerKill();
        min = p.contains("-") ? Double.parseDouble(p.split("-")[0]) : 0;
        return min > 0 ? min+random.nextInt((int) (Double.parseDouble(p.split("-")[1])-min+1)) : Double.parseDouble(p);
    }
    public RarityGem getConvertsTo() {
        return getRarityGem(getAddonConfig("soul trackers.yml").getString("soul trackers." + path + ".converts to gem"));
    }
    public ItemStack getItem() {
        if(is == null) is = api.d(getAddonConfig("soul trackers.yml"), "soul trackers." + path);
        return is.clone();
    }
    public String getApplied() { return ChatColor.translateAlternateColorCodes('&', getAddonConfig("soul trackers.yml").getString("soul trackers." + path + ".apply")); }
    public List<String> getApplyMsg() {
        if(apply == null) apply = api.colorizeListString(getAddonConfig("soul trackers.yml").getStringList("soul trackers." + path + ".apply msg"));
        return apply;
    }
    public List<String> getSplitMsg() {
        if(split == null) split = api.colorizeListString(getAddonConfig("soul trackers.yml").getStringList("soul trackers." + path + ".split msg"));
        return split;
    }
}
