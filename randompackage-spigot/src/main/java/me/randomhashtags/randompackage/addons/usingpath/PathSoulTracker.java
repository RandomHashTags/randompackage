package me.randomhashtags.randompackage.addons.usingpath;

import me.randomhashtags.randompackage.addons.RarityGem;
import me.randomhashtags.randompackage.addons.SoulTracker;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PathSoulTracker extends SoulTracker {
    public static YamlConfiguration soultrackersyml;
    private String path;
    private ItemStack is;
    private List<String> apply, split;
    public PathSoulTracker(String path) {
        this.path = path;
        addSoulTracker(getIdentifier(), this);
    }
    public String getIdentifier() { return path; }

    public String getTracks() { return soultrackersyml.getString("trackers." + path + ".tracks"); }
    public String[] getAppliesTo() { return soultrackersyml.getString("trackers." + path + ".applies to").split(";"); }
    public String getSoulsPerKill() { return soultrackersyml.getString("trackers." + path + ".souls per kill"); }
    public double getSoulsCollected() {
        double min = 0;
        final String p = getSoulsPerKill();
        min = p.contains("-") ? Double.parseDouble(p.split("-")[0]) : 0;
        return min > 0 ? min+random.nextInt((int) (Double.parseDouble(p.split("-")[1])-min+1)) : Double.parseDouble(p);
    }
    public RarityGem getConvertsTo() {
        return getRarityGem(soultrackersyml.getString("trackers." + path + ".converts to gem"));
    }
    public ItemStack getItem() {
        if(is == null) is = api.d(soultrackersyml, "trackers." + path);
        return is.clone();
    }
    public String getAppliedLore() { return ChatColor.translateAlternateColorCodes('&', soultrackersyml.getString("trackers." + path + ".apply")); }
    public List<String> getApplyMsg() {
        if(apply == null) apply = api.colorizeListString(soultrackersyml.getStringList("trackers." + path + ".apply msg"));
        return apply;
    }
    public List<String> getSplitMsg() {
        if(split == null) split = api.colorizeListString(soultrackersyml.getStringList("trackers." + path + ".split msg"));
        return split;
    }
}
