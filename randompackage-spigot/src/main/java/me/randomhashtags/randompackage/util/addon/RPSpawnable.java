package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.util.Spawnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.List;

public abstract class RPSpawnable extends RPAddon implements Spawnable {
    public static RegionPlugin spawnType = null;
    public List<String> getSpawnableFactionClaims() { return yml.getStringList("spawnable regions.faction claims"); }
    public boolean canSpawnAtFactionClaim(Player summoner, Location l) {
        if(l != null && hookedFactionsUUID()) {
            final String own = summoner != null ? factions.getFactionTag(summoner.getUniqueId()) : null, w = l.getWorld().getName(), f = factions.getRegionalIdentifierAt(l);
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
        if(spawnType == null) {
            final PluginManager pm = Bukkit.getPluginManager();
            if(pm.isPluginEnabled("Factions")) {
                spawnType = RegionPlugin.FACTIONS_UUID;
            } else if(pm.isPluginEnabled("ASkyblock")) {
                spawnType = RegionPlugin.ASKYBLOCK;
            } else if(pm.isPluginEnabled("SuperiorSkyblock")) {
                spawnType = RegionPlugin.SUPERIOR_SKYBLOCK;
            }
        }
        return spawnType;
    }
    // Skyblock
    public boolean canSpawnAtOwnedIsland() { return yml.getBoolean("spawnable regions.skyblock.own island"); }
    public boolean canSpawnAtCoopIsland() { return yml.getBoolean("spawnable regions.skyblock.coop island"); }
    public boolean canSpawnAtVisitingIsland() { return yml.getBoolean("spawnable regions.skyblock.while visiting"); }

    public boolean canSpawnSkyblock(RegionPlugin type, Location l) {
        // TODO
        return false;
    }

    public boolean canSpawnAt(Location l) { return canSpawnAt(null, l); }
    public boolean canSpawnAt(Player summoner, Location l) {
        final RegionPlugin a = getSpawnType();
        if(a == null) return true;
        switch(a) {
            case FACTIONS_UUID: return canSpawnAtFactionClaim(summoner, l);
            case ASKYBLOCK: return canSpawnSkyblock(RegionPlugin.ASKYBLOCK, l);
            case SUPERIOR_SKYBLOCK: return canSpawnSkyblock(RegionPlugin.SUPERIOR_SKYBLOCK, l);
            case EPIC_SKYBLOCK: return canSpawnSkyblock(RegionPlugin.EPIC_SKYBLOCK, l);
            default: return true;
        }
    }
    private enum RegionPlugin {
        FACTIONS_UUID, ASKYBLOCK, SUPERIOR_SKYBLOCK, EPIC_SKYBLOCK;
    }
}
