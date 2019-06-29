package me.randomhashtags.randompackage.utils.abstraction;

import java.util.Arrays;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public abstract class AbstractCustomEnchant extends Saveable {
    private List<String> lore;
    private String value;
    private List<String> appliesto;
    private int[] alchemist, tinkerer;

    public boolean isEnabled() { return yml.getBoolean("enabled"); }
    public String getName() { return yml.getString("name"); }
    public List<String> getLore() {
        if(lore == null) lore = api.colorizeListString(yml.getStringList("lore"));
        return lore;
    }
    public int getMaxLevel() { return yml.getInt("max level"); }
    public List<String> getAppliesTo() {
        if(appliesto == null) appliesto = Arrays.asList(yml.getString("applies to").split(";"));
        return appliesto;
    }
    public String getRequiredEnchant() { return yml.getString("requires"); }
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
        final int i = level-1;
        return i < getTinkerer().length ? tinkerer[i] : 0;
    }
    public String getEnchantProcValue() {
        if(value == null) {
            for(String s : getAttributes())
                if(s.toLowerCase().startsWith("enchantproc;value="))
                    value = s.toLowerCase().split("enchantproc;value=")[1];
        }
        return value;
    }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
