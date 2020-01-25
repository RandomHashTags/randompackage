package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.GlobalChallenge;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileGlobalChallenge extends RPAddon implements GlobalChallenge {
    private ItemStack display;
    public FileGlobalChallenge(File f) {
        load(f);
        if(isEnabled()) {
            register(Feature.GLOBAL_CHALLENGE, this);
        }
    }
    public String getIdentifier() { return getYamlName();  }

    public boolean isEnabled() { return yml.getBoolean("settings.enabled", false); }
    public ItemStack getItem() {
        if(display == null) display = API.d(yml, "item");
        return getClone(display);
    }
    public long getDuration() { return yml.getLong("settings.duration"); }
    public String getType() { return yml.getString("settings.type"); }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
