package me.randomhashtags.randompackage.recode.api.addons.usingFile;

import me.randomhashtags.randompackage.recode.api.addons.GlobalChallenge;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class FileGlobalChallenge extends GlobalChallenge {
    private ItemStack display;
    public FileGlobalChallenge(File f) {
        load(f);
        initilize();
    }
    public void initilize() { addGlobalChallenge(getYamlName(), this); }

    public ItemStack getDisplayItem() {
        if(display == null) display = api.d(yml, "item");
        return display.clone();
    }
    public String getTracks() { return yml.getString("settings.tracks"); }
    public long getDuration() { return yml.getLong("settings.duration"); }
    public String getType() { return yml.getString("settings.type"); }
}
