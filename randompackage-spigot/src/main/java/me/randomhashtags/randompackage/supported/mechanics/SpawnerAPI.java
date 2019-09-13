package me.randomhashtags.randompackage.supported.mechanics;

import me.randomhashtags.randompackage.RandomPackage;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public final class SpawnerAPI {
    private static SpawnerAPI instance;
    public static SpawnerAPI getSpawnerAPI() {
        if(instance == null) {
            instance = new SpawnerAPI();
            final String plugin = RandomPackage.spawner;
            instance.plugin = plugin;
            if(plugin != null && plugin.equals("SilkSpawners")) {
                instance.util = de.dustplanet.util.SilkUtil.hookIntoSilkSpanwers();
            }
        }
        return instance;
    }
    private String plugin;
    private Object util;

    public ItemStack getItem(String entitytype) {
        if(plugin != null) {
            switch (plugin) {
                case "EpicSpawners5": return es5(entitytype);
                case "EpicSpawners6": return es6(entitytype);
                case "SilkSpawners":  return ss(entitytype);
                default: return null;
            }
        }
        return null;
    }

    private ItemStack es5(String entitytype) {
        final String type = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        com.songoda.epicspawners.api.spawner.SpawnerData data = null;
        for(com.songoda.epicspawners.api.spawner.SpawnerData spawnerData : com.songoda.epicspawners.EpicSpawnersPlugin.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(type.equals(compare)) data = spawnerData;
        }
        return data != null ? data.toItemStack() : null;
    }
    private ItemStack es6(String entitytype) {
        final String type = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        com.songoda.epicspawners.spawners.spawner.SpawnerData data = null;
        for(com.songoda.epicspawners.spawners.spawner.SpawnerData spawnerData : com.songoda.epicspawners.EpicSpawners.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(type.equals(compare)) data = spawnerData;
        }
        return data != null ? data.toItemStack() : null;
    }
    private ItemStack ss(String entitytype) {
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
