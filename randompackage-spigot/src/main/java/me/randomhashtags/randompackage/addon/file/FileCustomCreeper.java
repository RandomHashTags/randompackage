package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomExplosion;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FileCustomCreeper extends RPAddon implements CustomExplosion {
    public static HashMap<UUID, FileCustomCreeper> living;
    private ItemStack item;
    public FileCustomCreeper(File f) {
        if(living == null) {
            living = new HashMap<>();
        }
        load(f);
        register(Feature.CUSTOM_EXPLOSION, this);
    }
    public String getIdentifier() { return "CREEPER_" + getYamlName(); }

    public String getCreeperName() { return colorize(yml.getString("creeper name")); }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return getClone(item);
    }

    public void spawn(Location l) {
        final Creeper c = l.getWorld().spawn(l, Creeper.class);
        c.setCustomName(getCreeperName());
        living.put(c.getUniqueId(), this);
    }
    public void didExplode(UUID uuid, List<Block> blockList) {
        living.remove(uuid);
    }
}
