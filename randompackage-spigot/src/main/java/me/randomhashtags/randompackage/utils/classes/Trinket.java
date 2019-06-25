package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Trinket {
    public static HashMap<String, Trinket> trinkets;
    private static RandomPackageAPI api;

    private YamlConfiguration yml;
    private String ymlName, radius, cooldown;
    private ItemStack item;
    private List<String> attributes;

    public Trinket(File f) {
        if(trinkets == null) {
            trinkets = new HashMap<>();
            api = RandomPackageAPI.getAPI();
        }
        yml = YamlConfiguration.loadConfiguration(f);
        ymlName = f.getName().split("\\.yml")[0];
        trinkets.put(ymlName, this);
    }

    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() { return ymlName; }

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
        if(attributes == null) attributes = yml.getStringList("attributes");
        return attributes;
    }

    public static void deleteAll() {
        trinkets = null;
        api = null;
    }
}
