package me.randomhashtags.randompackage.supported.mechanics;

import me.randomhashtags.randompackage.RandomPackage;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SpawnerAPI {
    INSTANCE;

    private Object util;

    SpawnerAPI() {
        final String plugin = RandomPackage.SPAWNER_PLUGIN_NAME;
        if(plugin != null && plugin.equals("SilkSpawners")) {
            util = de.dustplanet.util.SilkUtil.hookIntoSilkSpanwers();
        }
    }

    @Nullable
    public ItemStack getItem(@NotNull String entitytype) {
        switch (RandomPackage.SPAWNER_PLUGIN_NAME) {
            case "EpicSpawners5": return getItemES5(entitytype);
            case "EpicSpawners6": return getItemES6(entitytype);
            case "SilkSpawners":  return getItemSS(entitytype);
            default: return null;
        }
    }

    @Nullable
    public EntityType getType(@NotNull ItemStack is) {
        switch (RandomPackage.SPAWNER_PLUGIN_NAME) {
            case "EpicSpawners6": return getTypeES6(is);
            case "SilkSpawners": return getTypeSS(is);
            default: return null;
        }
    }

    @Nullable
    private ItemStack getItemES5(@NotNull String entitytype) {
        final String type = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        com.songoda.epicspawners.api.spawner.SpawnerData data = null;
        for(com.songoda.epicspawners.api.spawner.SpawnerData spawnerData : com.songoda.epicspawners.EpicSpawnersPlugin.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(type.equals(compare)) {
                data = spawnerData;
            }
        }
        return data != null ? data.toItemStack() : null;
    }

    @Nullable
    private EntityType getTypeES6(@NotNull ItemStack is) {
        final com.songoda.epicspawners.spawners.spawner.SpawnerData data = com.songoda.epicspawners.EpicSpawners.getInstance().getSpawnerManager().getSpawnerData(is);
        return data != null ? data.getEntities().get(0) : null;
    }
    @Nullable
    private ItemStack getItemES6(@NotNull String entitytype) {
        final String type = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        com.songoda.epicspawners.spawners.spawner.SpawnerData data = null;
        for(com.songoda.epicspawners.spawners.spawner.SpawnerData spawnerData : com.songoda.epicspawners.EpicSpawners.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(type.equals(compare)) {
                data = spawnerData;
            }
        }
        return data != null ? data.toItemStack() : null;
    }
    @Nullable
    private EntityType getTypeSS(@NotNull ItemStack is) {
        return EntityType.fromName(((de.dustplanet.util.SilkUtil) util).getStoredSpawnerItemEntityID(is));
    }
    @Nullable
    private ItemStack getItemSS(@NotNull String entitytype) {
        final String input = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        for(EntityType t : EntityType.values()) {
            if(input.equals(t.name().replace("_", "").replace(" ", ""))) {
                final String id = t.getName();
                return ((de.dustplanet.util.SilkUtil) util).newSpawnerItem(id, ((de.dustplanet.util.SilkUtil) util).getCustomSpawnerName(((de.dustplanet.util.SilkUtil) util).getCreatureName(id)), 1, false);
            }
        }
        return null;
    }
}
