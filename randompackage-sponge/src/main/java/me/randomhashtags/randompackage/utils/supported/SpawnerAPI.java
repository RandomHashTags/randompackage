package me.randomhashtags.randompackage.utils.supported;

import me.randomhashtags.randompackage.RandomPackage;
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
        }
        return null;
    }
}
