package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.abstraction.AbstractBooster;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Booster extends AbstractBooster {
    private ItemStack item;
    public Booster(File f) {
        load(f);
        created(getYamlName());
    }
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
