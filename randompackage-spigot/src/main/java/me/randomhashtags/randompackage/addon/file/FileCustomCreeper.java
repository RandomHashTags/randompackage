package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomExplosion;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class FileCustomCreeper extends RPAddonSpigot implements CustomExplosion {
    public static HashMap<UUID, FileCustomCreeper> LIVING;

    private final String name;
    private final List<String> attributes;
    private final ItemStack item;
    public FileCustomCreeper(File file) {
        super(file);
        if(LIVING == null) {
            LIVING = new HashMap<>();
        }
        final JSONObject json = parse_json_from_file(file);
        name = parse_string_in_json(json, "creeper name");
        attributes = parse_list_string_in_json(json, "attributes");
        item = create_item_stack(json, "item");
        register(Feature.CUSTOM_EXPLOSION, this);
    }
    @NotNull
    @Override
    public String getIdentifier() {
        return "CREEPER_" + identifier;
    }

    public String getCreeperName() {
        return name;
    }
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
    public @NotNull ItemStack getItem() {
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
