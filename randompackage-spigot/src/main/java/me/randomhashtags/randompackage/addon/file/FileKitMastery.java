package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.CustomKitMastery;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.api.addon.KitsMastery;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public final class FileKitMastery extends RPKitSpigot implements CustomKitMastery {
    private ItemStack item, redeem, shard, antiCrystal;
    private LinkedHashMap<CustomKit, Integer> requiredKits;
    public FileKitMastery(File f) {
        super(f);
        register(Feature.CUSTOM_KIT, this);
    }
    public Kits getKitClass() { return KitsMastery.getKitsMastery(); }

    public @NotNull String getName() {
        return getString(yml, "settings.name");
    }
    @NotNull
    @Override
    public ItemStack getItem() {
        if(item == null) item = set(createItemStack(yml, "gui settings"));
        return getClone(item);
    }
    public ItemStack getRedeem() {
        if(redeem == null) redeem = set(createItemStack(yml, "redeem"));
        return getClone(redeem);
    }
    private ItemStack set(ItemStack is) {
        if(is != null) {
            final ItemMeta m = is.getItemMeta();
            final List<String> l = new ArrayList<>();
            int req = 0;
            final HashMap<CustomKit, Integer> re = getRequiredKits();
            final CustomKit[] kitKeys = re.keySet().toArray(new CustomKit[re.size()]);
            for(String s : m.getLore()) {
                if(s.contains("{REQUIREMENT}")) {
                    final CustomKit kit = kitKeys[req];
                    final String name = kit.getItem().getItemMeta().getDisplayName();
                    s = s.replace("{REQUIREMENT}", name).replace("{TIER}", toRoman(re.get(kit)));
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
                requiredKits.put(getCustomKit(a[0]), Integer.parseInt(a[1]));
            }
        }
        return requiredKits;
    }
    public boolean losesRequiredKits() { return yml.getBoolean("settings.loses required kits"); }
    public ItemStack getShard() {
        if(shard == null) shard = createItemStack(yml, "shard");
        return getClone(shard);
    }
    public ItemStack getAntiCrystal() {
        if(antiCrystal == null) antiCrystal = createItemStack(yml, "anti crystal");
        return getClone(antiCrystal);
    }
    public List<String> getAntiCrystalNegatedEnchants() { return yml.getStringList("anti crystal.negate enchants"); }
    public String getAntiCrystalApplied() { return yml.getString("anti crystal.applied"); }
}
