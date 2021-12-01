package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.NotNull;
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

public final class FileCustomCreeper extends RPAddonSpigot implements CustomExplosion {
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

    public String getCreeperName() {
        return getString(yml, "creeper name");
    }
    public List<String> getAttributes() {
        return getStringList(yml, "attributes");
    }
    public ItemStack getItem() {
        if(item == null) item = createItemStack(yml, "item");
        return getClone(item);
    }

    public void spawn(@NotNull Location l) {
        final Creeper c = l.getWorld().spawn(l, Creeper.class);
        c.setCustomName(getCreeperName());
        living.put(c.getUniqueId(), this);
    }
    public void didExplode(UUID uuid, List<Block> blockList) {
        living.remove(uuid);
    }
}
