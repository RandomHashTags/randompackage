package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.CustomExplosion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class FileCustomCreeper extends CustomExplosion {
    public static HashMap<UUID, FileCustomCreeper> living;
    private ItemStack item;
    public FileCustomCreeper(File f) {
        load(f);
        initilize();
    }
    public void initilize() { addExplosion("CREEPER_" + getYamlName(), this); }
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

    public static void deleteAll() {
        living = null;
    }
}
