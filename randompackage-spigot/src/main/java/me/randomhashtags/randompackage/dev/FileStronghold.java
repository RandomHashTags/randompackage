package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.Stronghold;
import me.randomhashtags.randompackage.addon.enums.CaptureType;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.obj.PolyBoundary;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public abstract class FileStronghold extends RPAddonSpigot implements Stronghold {
    private ItemStack is;
    private Location center;
    private CaptureType captureType;
    private PolyBoundary squareZone;
    public FileStronghold(File f) {
        load(f);
        register(Feature.STRONGHOLD, this);
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        if(is == null) is = createItemStack(yml, "gui");
        return getClone(is);
    }
    public int getSlot() { return yml.getInt("gui.slot"); }

    public long getStartTime() { return 0; }
    public boolean allowsAFKCapturing() { return yml.getBoolean("settings.allows afk capturing"); }
    public Location getCenter() {
        if(center == null) center = toLocation(yml.getString("settings.center"));
        return center;
    }
    public CaptureType getCaptureType() {
        if(captureType == null) captureType = CaptureType.valueOf(yml.getString("settings.capture type").toUpperCase());
        return captureType;
    }
    public double getCaptureRadius() { return yml.getDouble("settings.capture radius"); }
    public PolyBoundary getSquareCaptureZone() {
        if(squareZone == null && getCaptureType().equals(CaptureType.SQUARE)) {
            squareZone = new PolyBoundary(getCenter(), (int) getCaptureRadius());
        }
        return squareZone;
    }
    public long getCaptureTime() { return 0; }
    public Player getCapturer() { return null; }
    public String getStatus() { return null; }
    public HashMap<String, String> getStatuses() { return null; }
    public double getControllingPercent() { return 0; }

    public List<String> getCapturedMsg() {
        return null;
    }
    public List<String> getNoLongerControllingMsg() { return getStringList(yml, "messages.no longer controlling"); }
    public List<String> getTakenControlMsg() { return getStringList(yml, "messages.taken control"); }

    public List<String> getRewards() { return yml.getStringList("rewards"); }
}
