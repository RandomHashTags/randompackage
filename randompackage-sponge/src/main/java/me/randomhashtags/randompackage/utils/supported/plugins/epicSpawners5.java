package me.randomhashtags.randompackage.utils.supported.plugins;

import com.songoda.epicspawners.EpicSpawnersPlugin;
import com.songoda.epicspawners.api.spawner.SpawnerData;
import org.spongepowered.api.item.inventory.ItemStack;

public class epicSpawners5 {

    private static epicSpawners5 instance;
    public static epicSpawners5 getEpicSpawners5() {
        if(instance == null) instance = new epicSpawners5();
        return instance;
    }

    public ItemStack getItem(String entitytype) {
        SpawnerData data = null;
        for(SpawnerData spawnerData : EpicSpawnersPlugin.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String input = entitytype.toUpperCase().replace("_", "").replace(" ", ""), compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(input.equals(compare)) data = spawnerData;
        }
        return data != null ? data.toItemStack() : null;
    }
}
