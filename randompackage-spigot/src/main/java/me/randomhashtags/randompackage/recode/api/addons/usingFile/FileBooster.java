package me.randomhashtags.randompackage.recode.api.addons.usingFile;

import me.randomhashtags.randompackage.recode.api.addons.Booster;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBooster extends Booster {
    private ItemStack item;
    public FileBooster(File f) {
        load(f);
        initilize();
    }
    public void initilize() { addBooster(getYamlName(), this); }

    public String getType() { return yml.getString("settings.type"); }
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
}
