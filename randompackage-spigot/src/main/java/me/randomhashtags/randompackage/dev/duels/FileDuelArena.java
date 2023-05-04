package me.randomhashtags.randompackage.dev.duels;

import me.randomhashtags.randompackage.addon.DuelArena;
import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileDuelArena extends RPAddonSpigot implements DuelArena {
    private final ItemStack item;
    private final MultilingualString name;
    private final List<Location> locations;

    public FileDuelArena(File f) {
        super(f);

        final JSONObject json = parse_json_from_file(f);
        item = create_item_stack(json, "item");
        name = parse_multilingual_string_in_json(json, "name");
        locations = new ArrayList<>();
        final List<String> location_strings = parse_list_string_in_json(json, "locations");
        for(String s : location_strings) {
            locations.add(string_to_location(s));
        }

        register(Feature.DUEL_ARENA, this);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return getClone(item);
    }
    @Override
    public @NotNull MultilingualString getName() {
        return name;
    }
    @Override
    public List<Location> getLocations() {
        return locations;
    }
}
