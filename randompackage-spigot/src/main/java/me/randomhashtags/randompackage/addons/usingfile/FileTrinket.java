package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.Trinket;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileTrinket extends Trinket {
    private String radius, cooldown;
    private ItemStack item;

    public FileTrinket(File f) {
        load(f);
        addTrinket(getIdentifier(), this);
    }
    public String getIdentifier() { return getYamlName(); }

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
        if(item == null) item = api.d(yml, "item");
        return item.clone();
    }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
