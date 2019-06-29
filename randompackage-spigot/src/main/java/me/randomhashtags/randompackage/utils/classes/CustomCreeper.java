package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomExplosion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class CustomCreeper extends AbstractCustomExplosion {
    public static HashMap<String, CustomCreeper> creepers;
    public static HashMap<UUID, CustomCreeper> living;

    private ItemStack item;
    public CustomCreeper(File f) {
        if(creepers == null) {
            creepers = new HashMap<>();
            living = new HashMap<>();
        }
        load(f);
        creepers.put(getYamlName(), this);
    }
    public String getCreeperName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("creeper name")); }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return item.clone();
    }

    public void spawn(Location l) {
        final Creeper c = l.getWorld().spawn(l, Creeper.class);
        c.setCustomName(getCreeperName());
        living.put(c.getUniqueId(), this);
    }
    public void didExplode(UUID uuid, List<Block> blockList) {
        living.remove(uuid);
    }

    public static CustomCreeper valueOf(ItemStack is) {
        if(creepers != null) {
            for(CustomCreeper c : creepers.values()) {
                if(c.getItem().isSimilar(is)) {
                    return c;
                }
            }
        }
        return null;
    }
    public static void deleteAll() {
        creepers = null;
        living = null;
    }
}
