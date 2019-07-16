package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.Booster;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBooster extends Booster {
    private ItemStack item;
    public FileBooster(File f) {
        load(f);
        addBooster(getIdentifier(), this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String getRecipients() { return yml.getString("settings.recipients"); }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return item.clone();
    }
    public ItemStack getItem(long duration, double multiplier) {
        final String d = api.getRemainingTime(duration), mu = Double.toString(api.round(multiplier, 4));
        final ItemStack i = getItem();
        final ItemMeta m = i.getItemMeta();
        final List<String> l = new ArrayList<>();
        for(String s : m.getLore()) {
            l.add(s.replace("{DURATION}", d).replace("{MULTIPLIER}", mu));
        }
        m.setLore(l);
        i.setItemMeta(m);
        return i;
    }
    public int getDurationLoreSlot() { return get("{DURATION}"); }
    public int getMultiplierLoreSlot() { return get("{MULTIPLIER}"); }
    private int get(String string) { return getItem().getItemMeta().getLore().indexOf(string); }

    public List<String> getActivateMsg() { return yml.getStringList("messages.activate"); }
    public List<String> getExpireMsg() { return yml.getStringList("messages.expire"); }
    public List<String> getNotifyMsg() { return yml.getStringList("messages.notify"); }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
