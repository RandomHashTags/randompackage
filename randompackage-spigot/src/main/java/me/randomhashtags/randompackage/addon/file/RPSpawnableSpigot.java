package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import me.randomhashtags.randompackage.addon.util.Spawnable;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.regional.FactionsUUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class RPSpawnableSpigot extends RPAddonSpigot implements Spawnable {
    public static RegionPlugin SPAWN_TYPE = null;
    public List<String> getSpawnableFactionClaims() { return yml.getStringList("spawnable regions.faction claims"); }
    public boolean canSpawnAtFactionClaim(@Nullable Player summoner, @NotNull Location l) {
        if(l != null && RegionalAPI.INSTANCE.hookedFactionsUUID()) {
            final String own = summoner != null ? RegionalAPI.INSTANCE.getFactionTag(summoner.getUniqueId()) : null, w = l.getWorld().getName(), f = FactionsUUID.INSTANCE.getRegionalIdentifierAt(l);
            for(String s : getSpawnableFactionClaims()) {
                if(s.startsWith(w + ";")) {
                    for(String r : s.split(w + ";")[1].split(";")) {
                        if(r.equalsIgnoreCase(f) || r.equalsIgnoreCase("own") && f.equals(own)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }
    public RegionPlugin getSpawnType() {
        if(SPAWN_TYPE == null) {
            if(PLUGIN_MANAGER.isPluginEnabled("Factions")) {
                SPAWN_TYPE = RegionPlugin.FACTIONS_UUID;
            } else if(PLUGIN_MANAGER.isPluginEnabled("ASkyblock")) {
                SPAWN_TYPE = RegionPlugin.ASKYBLOCK;
            } else if(PLUGIN_MANAGER.isPluginEnabled("SuperiorSkyblock")) {
                SPAWN_TYPE = RegionPlugin.SUPERIOR_SKYBLOCK;
            }
        }
        return SPAWN_TYPE;
    }
    // Skyblock
    public boolean canSpawnAtOwnedIsland() {
        return yml.getBoolean("spawnable regions.skyblock.own island");
    }
    public boolean canSpawnAtCoopIsland() {
        return yml.getBoolean("spawnable regions.skyblock.coop island");
    }
    public boolean canSpawnAtVisitingIsland() {
        return yml.getBoolean("spawnable regions.skyblock.while visiting");
    }

    public boolean canSpawnSkyblock(RegionPlugin type, Location l) {
        // TODO
        return false;
    }

    public boolean canSpawnAt(@NotNull Location l) {
        return canSpawnAt(null, l);
    }
    public boolean canSpawnAt(@Nullable Player summoner, @NotNull Location l) {
        final RegionPlugin spawnPlugin = getSpawnType();
        if(spawnPlugin == null) {
            return true;
        }
        switch(spawnPlugin) {
            case FACTIONS_UUID:
                return canSpawnAtFactionClaim(summoner, l);
            case ASKYBLOCK:
                return canSpawnSkyblock(RegionPlugin.ASKYBLOCK, l);
            case SUPERIOR_SKYBLOCK:
                return canSpawnSkyblock(RegionPlugin.SUPERIOR_SKYBLOCK, l);
            case EPIC_SKYBLOCK:
                return canSpawnSkyblock(RegionPlugin.EPIC_SKYBLOCK, l);
            default:
                return true;
        }
    }
    private enum RegionPlugin {
        FACTIONS_UUID, ASKYBLOCK, SUPERIOR_SKYBLOCK, EPIC_SKYBLOCK
    }
}
