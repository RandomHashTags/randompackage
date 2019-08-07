package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public class SpawnerStacking extends RPFeature {
    private static SpawnerStacking instance;
    public static SpawnerStacking getSpawnerStacking() {
        if(instance == null) instance = new SpawnerStacking();
        return instance;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "spawner stacking.yml");
        final YamlConfiguration config = getRPConfig(null, "spawner stacking.yml");


        sendConsoleMessage("&6[RandomPackage] &aLoaded Spawner Stacking &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
