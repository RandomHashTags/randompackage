package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Booster;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileBooster extends RPAddon implements Booster {
    private ItemStack item;
    public FileBooster(File f) {
        load(f);
        register(Feature.BOOSTER, this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String getRecipients() { return yml.getString("settings.recipients"); }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return getClone(item);
    }

    public int getTimeLoreSlot() { return get("{TIME}"); }
    public int getMultiplierLoreSlot() { return get("{MULTIPLIER}"); }
    private int get(String string) {
        final List<String> l = getItem().getItemMeta().getLore();
        for(int i = 0; i < l.size(); i++) {
            if(l.get(i).contains(string)) {
                return i;
            }
        }
        return -1;
    }
    public List<String> getActivateMsg() { return yml.getStringList("messages.activate"); }
    public List<String> getExpireMsg() { return yml.getStringList("messages.expire"); }
    public List<String> getNotifyMsg() { return yml.getStringList("messages.notify"); }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
