package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.SoulTracker;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileSoulTracker extends SoulTracker {
    private ItemStack is;
    private List<String> apply, split;
    public FileSoulTracker(File f) {
        load(f);
        initilize();
    }
    public void initilize() { addSoulTracker(getYamlName(), this); }

    public String getTracks() { return yml.getString("tracks"); }
    public String[] getAppliesTo() { return yml.getString("applies to").split(";"); }
    public String getSoulsPerKill() { return yml.getString("souls per kill"); }
    public double getSoulsCollected() {
        double min = 0, amount = 0;
        final String p = getSoulsPerKill();
        min = p.contains("-") ? Double.parseDouble(p.split("-")[0]) : 0;
        return min > 0 ? min+random.nextInt((int) (Double.parseDouble(p.split("-")[1])-min+1)) : amount;
    }
    public FileRarityGem getConvertsTo() {
        return FileRarityGem.gems.getOrDefault(yml.getString("converts to gem"), null);
    }
    public ItemStack getItem() {
        if(is == null) is = api.d(yml, "item");
        return is.clone();
    }
    public String getAppliedLore() { return ChatColor.translateAlternateColorCodes('&', yml.getString("applied lore")); }
    public List<String> getApplyMsg() {
        if(apply == null) apply = api.colorizeListString(yml.getStringList("messages.apply"));
        return apply;
    }
    public List<String> getSplitMsg() {
        if(split == null) split = api.colorizeListString(yml.getStringList("messages.split"));
        return split;
    }
}
