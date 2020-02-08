package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Booster;
import me.randomhashtags.randompackage.addon.BoosterRecipients;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileBooster extends RPAddon implements Booster {
    private ItemStack item;
    private BoosterRecipients recipients;
    public FileBooster(File f) {
        load(f);
        register(Feature.BOOSTER, this);
    }
    public String getIdentifier() { return getYamlName(); }

    public BoosterRecipients getRecipients() {
        if(recipients == null) {
            final String target = yml.getString("settings.recipients");
            recipients = target != null && !target.isEmpty() ? BoosterRecipients.valueOf(target.toUpperCase()) : BoosterRecipients.SELF;
        }
        return recipients;
    }
    public ItemStack getItem() {
        if(item == null) item = API.createItemStack(yml, "item");
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
    public List<String> getActivateMsg() {
        return getStringList(yml, "messages.activate");
    }
    public List<String> getExpireMsg() {
        return getStringList(yml, "messages.expire");
    }
    public List<String> getNotifyMsg() {
        return getStringList(yml, "messages.notify");
    }
    public List<String> getAttributes() {
        return getStringList(yml, "attributes");
    }
}
