package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;

public class SpawnerStacking extends RPFeature {
    private static SpawnerStacking instance;
    public static SpawnerStacking getSpawnerStacking() {
        if(instance == null) instance = new SpawnerStacking();
        return instance;
    }

    private HashMap<Location, Integer> stacks;

    public String getIdentifier() { return "SPAWNER_STACKING"; }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "spawner stacking.yml");
        final YamlConfiguration config = getRPConfig(null, "spawner stacking.yml");

        stacks = new HashMap<>();

        sendConsoleMessage("&6[RandomPackage] &aLoaded Spawner Stacking &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final Material m = event.getBlock().getType();
        if(m.name().equals(UMaterial.SPAWNER.getVersionName())) {

        }
    }
}
