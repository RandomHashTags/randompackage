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
            instance.plugin = plugin == null ? "" : plugin;
            if(plugin != null && plugin.equals("SilkSpawners")) {
                instance.util = de.dustplanet.util.SilkUtil.hookIntoSilkSpanwers();
            }
        }
        return instance;
    }
    private String plugin;
    private Object util;

    public ItemStack getItem(String entitytype) {
        switch (plugin) {
            case "EpicSpawners5": return getItemES5(entitytype);
            case "EpicSpawners6": return getItemES6(entitytype);
            case "SilkSpawners":  return getItemSS(entitytype);
            default: return null;
        }
    }
    public EntityType getType(ItemStack is) {
        switch (plugin) {
            case "EpicSpawners6": return getTypeES6(is);
            case "SilkSpawners": return getTypeSS(is);
            default: return null;
        }
    }

    private ItemStack getItemES5(String entitytype) {
        final String type = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        com.songoda.epicspawners.api.spawner.SpawnerData data = null;
        for(com.songoda.epicspawners.api.spawner.SpawnerData spawnerData : com.songoda.epicspawners.EpicSpawnersPlugin.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(type.equals(compare)) data = spawnerData;
        }
        return data != null ? data.toItemStack() : null;
    }

    private EntityType getTypeES6(ItemStack is) {
        final com.songoda.epicspawners.spawners.spawner.SpawnerData data = com.songoda.epicspawners.EpicSpawners.getInstance().getSpawnerManager().getSpawnerData(is);
        return data != null ? data.getEntities().get(0) : null;
    }
    private ItemStack getItemES6(String entitytype) {
        final String type = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        com.songoda.epicspawners.spawners.spawner.SpawnerData data = null;
        for(com.songoda.epicspawners.spawners.spawner.SpawnerData spawnerData : com.songoda.epicspawners.EpicSpawners.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(type.equals(compare)) data = spawnerData;
        }
        return data != null ? data.toItemStack() : null;
    }

    private EntityType getTypeSS(ItemStack is) {
        return EntityType.fromId(((de.dustplanet.util.SilkUtil) util).getStoredSpawnerItemEntityID(is));
    }
    private ItemStack getItemSS(String entitytype) {
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
