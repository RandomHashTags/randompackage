package me.randomhashtags.randompackage.utils.supported;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.utils.supported.plugins.epicSpawners5;
import me.randomhashtags.randompackage.utils.supported.plugins.epicSpawners6;
import me.randomhashtags.randompackage.utils.supported.plugins.silkSpawners;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpawnerAPI {

    private static SpawnerAPI instance;
    public static SpawnerAPI getSpawnerAPI() {
        if(instance == null) {
            instance = new SpawnerAPI();
            plugin = RandomPackage.spawner;
        }
        return instance;
    }
    private static String plugin;

    public ItemStack getItem(String entitytype) {
        if(plugin != null) {
            if(plugin.equals("EpicSpawners5")) return epicSpawners5.getEpicSpawners5().getItem(entitytype);
            else if(plugin.equals("EpicSpawners6")) return epicSpawners6.getEpicSpawners6().getItem(entitytype);
            else if(plugin.equals("SilkSpawners")) return silkSpawners.getSilkSpawners().getItem(entitytype);
        }
        return null;
    }
}
