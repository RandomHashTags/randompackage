package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.CustomKit;
import me.randomhashtags.randompackage.addons.utils.RPKit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class FileKitMastery extends RPKit {
    private int antiCrystalPercentSlot;
    private ItemStack item, redeem, shard, antiCrystal;
    private LinkedHashMap<CustomKit, Integer> requiredKits;
    private List<String> antiCrystalAddedLore;
    public FileKitMastery(File f) {
        load(f);
        addKit(getIdentifier(), this);
    }
    public String getIdentifier() { return "MASTERY_" + getYamlName(); }

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
        return shard.clone();
    }
    public ItemStack getAntiCrystal() {
        if(antiCrystal == null) {
            antiCrystal = api.d(yml, "anti crystal");
            int i = 0;
            for(String s : antiCrystal.getItemMeta().getLore()) {
                if(s.contains("{PERCENT}")) antiCrystalPercentSlot = i;
                i++;
            }
        }
        return antiCrystal.clone();
    }
    public int getAntiCrystalPercentSlot() {
        if(antiCrystal == null) getAntiCrystal();
        return antiCrystalPercentSlot;
    }
    public ItemStack getAntiCrystal(int percent) {
        final ItemStack i = getAntiCrystal();
        final ItemMeta m = i.getItemMeta();
        final List<String> l = m.getLore();
        for(String s : m.getLore()) l.add(s.replace("{PERCENT}", Integer.toString(percent)));
        m.setLore(l);
        i.setItemMeta(m);
        return i;
    }
    public List<String> getAntiCrystalNegatedEnchants() { return yml.getStringList("anti crystal.negate enchants"); }
    public List<String> getAntiCrystalAddedLore() {
        if(antiCrystalAddedLore == null) antiCrystalAddedLore = api.colorizeListString(yml.getStringList("anti crystal.added lore"));
        return antiCrystalAddedLore;
    }

    public static FileKitMastery valueOfRedeem(ItemStack is) {
        if(kits != null && is != null) {
            final Class a = FileKitMastery.class;
            for(CustomKit k : kits.values()) {
                if(k.getClass().isInstance(a)) {
                    final FileKitMastery m = (FileKitMastery) k;
                    final ItemStack r = m.getRedeem();
                    if(r != null && r.isSimilar(is)) {
                        return m;
                    }
                }
            }
        }
        return null;
    }
}
