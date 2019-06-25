package me.randomhashtags.randompackage.utils.supported.plugins;

import org.spongepowered.api.item.inventory.ItemStack;

public class epicSpawners6 {

    private static epicSpawners6 instance;
    public static epicSpawners6 getEpicSpawners6() {
        if(instance == null) instance = new epicSpawners6();
        return instance;
    }

    public ItemStack getItem(String entitytype) {
        SpawnerData data = null;
        for(SpawnerData spawnerData : EpicSpawners.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String input = entitytype.toUpperCase().replace("_", "").replace(" ", ""), compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(input.equals(compare)) data = spawnerData;
        }
        return data != null ? data.toItemStack() : null;
    }
}
