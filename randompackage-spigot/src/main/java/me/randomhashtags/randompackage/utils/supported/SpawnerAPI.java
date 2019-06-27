package me.randomhashtags.randompackage.utils.supported;

import me.randomhashtags.randompackage.RandomPackage;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class SpawnerAPI {

    private static SpawnerAPI instance;
    public static SpawnerAPI getSpawnerAPI() {
        if(instance == null) {
            instance = new SpawnerAPI();
            plugin = RandomPackage.spawner;
            if(plugin != null && plugin.equals("SilkSpawners")) {
                util = de.dustplanet.util.SilkUtil.hookIntoSilkSpanwers();
            }
        }
        return instance;
    }
    private static String plugin;
    private static Object util;

    public ItemStack getItem(String entitytype) {
        if(plugin != null) {
            if(plugin.equals("EpicSpawners5")) return getItemEpicSpawners5(entitytype);
            else if(plugin.equals("EpicSpawners6")) return getItemEpicSpawners6(entitytype);
            else if(plugin.equals("SilkSpawners")) return getItemSilkSpawners(entitytype);
        }
        return null;
    }

    public ItemStack getItemEpicSpawners5(String entitytype) {
        com.songoda.epicspawners.api.spawner.SpawnerData data = null;
        for(com.songoda.epicspawners.api.spawner.SpawnerData spawnerData : com.songoda.epicspawners.EpicSpawnersPlugin.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String input = entitytype.toUpperCase().replace("_", "").replace(" ", ""), compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(input.equals(compare)) data = spawnerData;
        }
        return data != null ? data.toItemStack() : null;
    }
    public ItemStack getItemEpicSpawners6(String entitytype) {
        com.songoda.epicspawners.spawners.spawner.SpawnerData data = null;
        for(com.songoda.epicspawners.spawners.spawner.SpawnerData spawnerData : com.songoda.epicspawners.EpicSpawners.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String input = entitytype.toUpperCase().replace("_", "").replace(" ", ""), compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(input.equals(compare)) data = spawnerData;
        }
        return data != null ? data.toItemStack() : null;
    }
    public ItemStack getItemSilkSpawners(String entitytype) {
        final String input = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        for(EntityType t : EntityType.values()) {
            if(input.equals(t.name().replace("_", "").replace(" ", ""))) {
                final short id = t.getTypeId();
                return ((de.dustplanet.util.SilkUtil) util).newSpawnerItem(id, ((de.dustplanet.util.SilkUtil) util).getCustomSpawnerName(((de.dustplanet.util.SilkUtil) util).getCreatureName(id)), 1, false);
            }
        }
        return null;
    }
}
