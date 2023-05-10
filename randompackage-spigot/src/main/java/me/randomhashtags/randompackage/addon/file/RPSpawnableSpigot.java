package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.util.Spawnable;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.regional.FactionsUUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class RPSpawnableSpigot extends RPAddonSpigot implements Spawnable {

    private final List<String> spawnable_faction_claims;
    private final boolean can_spawn_at_owned_island, can_spawn_on_coop_island, can_spawn_while_visiting_island;
    public static RegionPlugin SPAWN_TYPE = null;

    public RPSpawnableSpigot(@Nullable File file) {
        super(file);
        final JSONObject json = file != null ? parse_json_from_file(file) : new JSONObject(); // TODO: print to console?

        final JSONObject spawnable_regions_json = parse_json_in_json(json, "spawnable regions");
        spawnable_faction_claims = parse_list_string_in_json(spawnable_regions_json, "faction claims");
        final JSONObject spawnable_regions_skyblock_json = parse_json_in_json(spawnable_regions_json, "skyblock");
        can_spawn_at_owned_island = parse_boolean_in_json(spawnable_regions_skyblock_json, "own island");
        can_spawn_on_coop_island = parse_boolean_in_json(spawnable_regions_skyblock_json, "coop island");
        can_spawn_while_visiting_island = parse_boolean_in_json(spawnable_regions_skyblock_json, "while visiting");
    }

    public List<String> getSpawnableFactionClaims() {
        return spawnable_faction_claims;
    }
    public boolean canSpawnAtFactionClaim(@Nullable Player summoner, @NotNull Location l) {
        final RegionalAPI regional = RegionalAPI.INSTANCE;
        if(regional.hookedFactionsUUID()) {
            final String own = summoner != null ? regional.getFactionTag(summoner.getUniqueId()) : null, world_name = l.getWorld().getName(), f = FactionsUUID.INSTANCE.getRegionalIdentifierAt(l);
            for(String spawnable_faction_claim : getSpawnableFactionClaims()) {
                if(spawnable_faction_claim.startsWith(world_name + ";")) {
                    for(String r : spawnable_faction_claim.split(world_name + ";")[1].split(";")) {
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
        return can_spawn_at_owned_island;
    }
    public boolean canSpawnAtCoopIsland() {
        return can_spawn_on_coop_island;
    }
    public boolean canSpawnAtVisitingIsland() {
        return can_spawn_while_visiting_island;
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
