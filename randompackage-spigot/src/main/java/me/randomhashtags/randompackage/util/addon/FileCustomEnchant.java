package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.CustomEnchant;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class FileCustomEnchant extends RPAddon implements CustomEnchant {
    private List<String> lore;
    private String value;
    private List<String> appliesto;
    private BigDecimal[] alchemist, tinkerer;

    public FileCustomEnchant(File f) {
        load(f);
        addEnchant(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public boolean isEnabled() { return yml.getBoolean("enabled"); }
    public String getName() { return yml.getString("name"); }
    public List<String> getLore() {
        if(lore == null) lore = colorizeListString(yml.getStringList("lore"));
        return lore;
    }
    public int getMaxLevel() { return yml.getInt("max level"); }
    public List<String> getAppliesTo() {
        if(appliesto == null) appliesto = Arrays.asList(yml.getString("applies to").split(";"));
        return appliesto;
    }
    public String getRequiredEnchant() { return yml.getString("requires"); }
    public BigDecimal[] getAlchemist() {
        if(alchemist == null) {
            final String[] a = yml.getString("alchemist").split(":");
            final BigDecimal[] alchemist = new BigDecimal[a.length];
            int i = 0;
            for(String s : a) {
                alchemist[i] = BigDecimal.valueOf(Integer.parseInt(s));
                i++;
            }
            this.alchemist = alchemist;
        }
        return alchemist;
    }
    public BigDecimal[] getTinkerer() {
        if(tinkerer == null) {
            final String[] t = yml.getString("tinkerer").split(":");
            final BigDecimal[] tinkerer = new BigDecimal[t.length];
            int i = 0;
            for(String s : t) {
                tinkerer[i] = BigDecimal.valueOf(Integer.parseInt(s));
                i++;
            }
            this.tinkerer = tinkerer;
        }
        return tinkerer;
    }
    public String getEnchantProcValue() {
        if(value == null) {
            for(String s : getAttributes()) {
                final String l = s.toLowerCase();
                if(l.startsWith("enchantproc;value="))
                    value = l.split("enchantproc;value=")[1];
            }
        }
        return value;
    }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
