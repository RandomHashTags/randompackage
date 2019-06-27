package me.randomhashtags.randompackage.utils.classes.customenchants;

import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class CustomEnchant {
    public static TreeMap<String, CustomEnchant> enabled, disabled;

    private YamlConfiguration yml;
    private String ymlName, name, value, requiredEnchant;
    private boolean isEnabled;
    private List<String> lore, attributes, appliesto;
    private int maxlevel;
    private int[] alchemist, tinkerer;
    public CustomEnchant(File f) {
        if(enabled == null) {
            enabled = new TreeMap<>();
            disabled = new TreeMap<>();
        }
        yml = YamlConfiguration.loadConfiguration(f);
        ymlName = f.getName().split("\\.yml")[0];
        isEnabled = yml.getBoolean("enabled");
        maxlevel = 0;

        if(isEnabled) {
            enabled.put(ymlName, this);
        } else {
            disabled.put(ymlName, this);
        }
    }
    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() { return ymlName; }
    public String getName() {
        if(name == null) name = yml.getString("name");
        return name;
    }
    public boolean isEnabled() { return isEnabled; }
    public List<String> getLore() {
        if(lore == null) lore = api.colorizeListString(yml.getStringList("lore"));
        return lore;
    }
    public List<String> getAppliesTo() {
        if(appliesto == null) appliesto = Arrays.asList(yml.getString("applies to").split(";"));
        return appliesto;
    }
    public String getRequiredEnchant() {
        if(requiredEnchant == null) requiredEnchant = yml.getString("requires");
        return requiredEnchant;
    }
    private void loadAttributes() {
        if(attributes == null) attributes = yml.getStringList("attributes");
    }
    public List<String> getAttributes() {
        loadAttributes();
        return attributes;
    }
    public int getMaxLevel() {
        if(maxlevel == 0) maxlevel = yml.getInt("max level");
        return maxlevel;
    }
    public int[] getAlchemist() {
        if(alchemist == null) {
            final String[] a = yml.getString("alchemist").split(":");
            final int[] alchemist = new int[a.length];
            int i = 0;
            for(String s : a) {
                alchemist[i] = Integer.parseInt(s);
                i++;
            }
            this.alchemist = alchemist;
        }
        return alchemist;
    }
    public int getAlchemistUpgradeCost(int level) {
        final int i = level-1;
        return i < getAlchemist().length ? alchemist[i] : 0;
    }
    public int[] getTinkerer() {
        if(tinkerer == null) {
            final String[] t = yml.getString("tinkerer").split(":");
            final int[] tinkerer = new int[t.length];
            int i = 0;
            for(String s : t) {
                tinkerer[i] = Integer.parseInt(s);
                i++;
            }
            this.tinkerer = tinkerer;
        }
        return tinkerer;
    }
    public int getTinkererValue(int level) {
        return getTinkerer()[level-1];
    }
    public String getEnchantProcValue() {
        if(value == null) {
            for(String s : getAttributes())
                if(s.toLowerCase().startsWith("enchantproc;value="))
                    value = s.toLowerCase().split("enchantproc;value=")[1];
        }
        return value;
    }

    public static CustomEnchant valueOf(String string) {
        if(enabled != null && string != null) {
            final String s = ChatColor.stripColor(string);
            for(CustomEnchant ce : enabled.values())
                if(s.startsWith(ChatColor.stripColor(ce.getName())))
                    return ce;
            }
        return null;
    }
    public static CustomEnchant valueOf(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomEnchant e = valueOf(is.getItemMeta().getDisplayName());
            final EnchantRarity r = EnchantRarity.valueOf(e);
            return e != null && UMaterial.match(is).equals(UMaterial.match(r.getRevealedItem())) ? e : null;
        }
        return null;
    }
    public static void deleteAll() {
        enabled = null;
        disabled = null;
    }
}
