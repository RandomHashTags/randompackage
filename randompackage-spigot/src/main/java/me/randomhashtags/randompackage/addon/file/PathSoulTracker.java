package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.addon.SoulTracker;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PathSoulTracker extends RPAddonSpigot implements SoulTracker {
    private final String path;
    private ItemStack is;
    private List<String> apply, split;
    public PathSoulTracker(String path) {
        super(null);
        this.path = path;
        register(Feature.SOUL_TRACKER, this);
    }
    @NotNull
    @Override
    public String getIdentifier() {
        return path;
    }

    public boolean canBeApplied(@NotNull ItemStack is) {
        final String m = is.getType().name();
        for(String s : getAppliesTo()) {
            if(m.toUpperCase().endsWith(s)) {
                return true;
            }
        }
        return false;
    }
    public String getTracks() {
        return getAddonConfig("soul trackers.yml").getString("soul trackers." + path + ".tracks");
    }
    public @NotNull List<String> getAppliesTo() {
        return getAddonConfig("soul trackers.yml").getStringList("soul trackers." + path + ".applies to");
    }
    public String getSoulsPerKill() {
        return getAddonConfig("soul trackers.yml").getString("soul trackers." + path + ".souls per kill");
    }
    public double getSoulsCollected() {
        double min = 0;
        final String p = getSoulsPerKill();
        min = p.contains("-") ? Double.parseDouble(p.split("-")[0]) : 0;
        return min > 0 ? min+RANDOM.nextInt((int) (Double.parseDouble(p.split("-")[1])-min+1)) : Double.parseDouble(p);
    }
    public RarityGem getConvertsTo() {
        return getRarityGem(getAddonConfig("soul trackers.yml").getString("soul trackers." + path + ".converts to gem"));
    }
    public @NotNull ItemStack getItem() {
        if(is == null) is = createItemStack(getAddonConfig("soul trackers.yml"), "soul trackers." + path);
        return getClone(is);
    }
    public @NotNull String getAppliedString() { return colorize(getAddonConfig("soul trackers.yml").getString("soul trackers." + path + ".apply")); }
    public List<String> getApplyMsg() {
        if(apply == null) apply = colorizeListString(getAddonConfig("soul trackers.yml").getStringList("soul trackers." + path + ".apply msg"));
        return apply;
    }
    public List<String> getSplitMsg() {
        if(split == null) split = colorizeListString(getAddonConfig("soul trackers.yml").getStringList("soul trackers." + path + ".split msg"));
        return split;
    }
}
