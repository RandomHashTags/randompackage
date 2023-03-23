package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomExplosion;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class FileCustomCreeper extends RPAddonSpigot implements CustomExplosion {
    public static HashMap<UUID, FileCustomCreeper> LIVING;
    private ItemStack item;
    public FileCustomCreeper(File file) {
        if(LIVING == null) {
            LIVING = new HashMap<>();
        }
        load(file);
        register(Feature.CUSTOM_EXPLOSION, this);
    }
    @NotNull
    @Override
    public String getIdentifier() {
        return "CREEPER_" + getYamlName();
    }

    public String getCreeperName() {
        return getString(yml, "creeper name");
    }
    public List<String> getAttributes() {
        return getStringList(yml, "attributes");
    }
    public @NotNull ItemStack getItem() {
        if(item == null) item = createItemStack(yml, "item");
        return getClone(item);
    }

    public void spawn(@NotNull Location location) {
        final Creeper creeper = location.getWorld().spawn(location, Creeper.class);
        creeper.setCustomName(getCreeperName());
        LIVING.put(creeper.getUniqueId(), this);
    }
    public void didExplode(UUID uuid, List<Block> blockList) {
        LIVING.remove(uuid);
    }
}
