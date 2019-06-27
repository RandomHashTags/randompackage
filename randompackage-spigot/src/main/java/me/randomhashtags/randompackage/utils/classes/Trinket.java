package me.randomhashtags.randompackage.utils.classes;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class Trinket {
    public static HashMap<String, Trinket> trinkets;

    private File f;
    private YamlConfiguration yml;
    private String radius, cooldown;
    private ItemStack item;

    public Trinket(File f) {
        if(trinkets == null) {
            trinkets = new HashMap<>();
        }
        this.f = f;
        yml = YamlConfiguration.loadConfiguration(f);
        trinkets.put(getYamlName(), this);
    }

    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() { return f.getName().split("\\.yml")[0]; }

    public String getRadius() {
        if(radius == null) {
            final String s = yml.getString("settings.radius");
            radius = s == null ? "0" : s;
        }
        return radius;
    }
    public String getCooldown() {
        if(cooldown == null) {
            final String s = yml.getString("settings.cooldown");
            cooldown = s == null ? "0" : s;
        }
        return cooldown;
    }
    public ItemStack getItem() {
        if(item == null) {
            item = api.d(yml, "item");
        }
        return item.clone();
    }
    public List<String> getAttributes() {
        return yml.getStringList("attributes");
    }

    public static void deleteAll() {
        trinkets = null;
    }
}
