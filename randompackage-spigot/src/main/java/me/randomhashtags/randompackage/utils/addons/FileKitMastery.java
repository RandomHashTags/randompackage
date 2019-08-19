package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.CustomKit;
import me.randomhashtags.randompackage.addons.CustomKitMastery;
import me.randomhashtags.randompackage.addons.Kits;
import me.randomhashtags.randompackage.api.addons.KitsMastery;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class FileKitMastery extends RPKit implements CustomKitMastery {
    private ItemStack item, redeem, shard, antiCrystal;
    private LinkedHashMap<CustomKit, Integer> requiredKits;
    public FileKitMastery(File f) {
        load(f);
        addKit(getIdentifier(), this);
    }
    public String getIdentifier() { return getYamlName(); }
    public Kits getKitClass() { return KitsMastery.getKitsMastery(); }

    public String getName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("settings.name")); }
    public ItemStack getItem() {
        if(item == null) item = set(api.d(yml, "gui settings"));
        return item.clone();
    }
    public ItemStack getRedeem() {
        if(redeem == null) redeem = set(api.d(yml, "redeem"));
        return redeem != null ? redeem.clone() : null;
    }
    private ItemStack set(ItemStack is) {
        if(is != null) {
            final ItemMeta m = is.getItemMeta();
            final List<String> l = new ArrayList<>();
            int req = 0;
            final HashMap<CustomKit, Integer> re = getRequiredKits();
            for(String s : m.getLore()) {
                if(s.contains("{REQUIREMENT}")) {
                    final CustomKit kit = (CustomKit) re.keySet().toArray()[req];
                    final String name = kit.getItem().getItemMeta().getDisplayName();
                    s = s.replace("{REQUIREMENT}", name).replace("{TIER}", api.toRoman(re.get(kit)));
                    req++;
                }
                l.add(s);
                m.setLore(l);
                is.setItemMeta(m);
            }
        }
        return is;
    }
    public LinkedHashMap<CustomKit, Integer> getRequiredKits() {
        if(requiredKits == null) {
            requiredKits = new LinkedHashMap<>();
            final List<String> R = yml.getStringList("required kits");
            for(String s : R) {
                final String[] a = s.split(";");
                requiredKits.put(getKit(a[0]), Integer.parseInt(a[1]));
            }
        }
        return requiredKits;
    }
    public boolean losesRequiredKits() { return yml.getBoolean("settings.loses required kits"); }
    public ItemStack getShard() {
        if(shard == null) shard = api.d(yml, "shard");
        return shard != null ? shard.clone() : null;
    }
    public ItemStack getAntiCrystal() {
        if(antiCrystal == null) antiCrystal = api.d(yml, "anti crystal");
        return antiCrystal != null ? antiCrystal.clone() : null;
    }
    public List<String> getAntiCrystalNegatedEnchants() {
        return yml.getStringList("anti crystal.negate enchants");
    }
    public String getAntiCrystalApplied() {
        return yml.getString("anti crystal.applied");
    }
}
