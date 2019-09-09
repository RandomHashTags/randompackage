package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.GlobalChallenge;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileGlobalChallenge extends RPAddon implements GlobalChallenge {
    private ItemStack display;
    public FileGlobalChallenge(File f) {
        load(f);
        addGlobalChallenge(this);
    }
    public String getIdentifier() { return getYamlName();  }

    public ItemStack getItem() {
        if(display == null) display = api.d(yml, "item");
        return display.clone();
    }
    public long getDuration() { return yml.getLong("settings.duration"); }
    public String getType() { return yml.getString("settings.type"); }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
