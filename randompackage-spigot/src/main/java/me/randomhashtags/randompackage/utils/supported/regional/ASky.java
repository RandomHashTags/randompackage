package me.randomhashtags.randompackage.utils.supported.regional;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.supported.Regional;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class ASky extends RPFeature implements Regional {
    private static ASky instance;
    public static ASky getASkyblock() {
        if(instance == null) instance = new ASky();
        return instance;
    }

    private ASkyBlockAPI api;

    public String getIdentifier() { return "REGIONAL_ASKYBLOCK"; }
    public void load() {
        api = ASkyBlockAPI.getInstance();
    }
    public void unload() {
        api = null;
    }

    private boolean areTeammates(UUID player1, UUID player2) { return api.inTeam(player1) && api.getTeamMembers(player1).contains(player2); }

    public boolean canModify(UUID player, Location location) {
        final Island i = api.getIslandAt(location);
        return i != null && (i.getMembers().contains(player) || areTeammates(i.getOwner(), player));
    }

    public boolean areEnemies(UUID player1, UUID player2) { return false; }
    public List<UUID> getAssociates(UUID player) {
        final Island is = api.getIslandOwnedBy(player);
        return is != null ? is.getMembers() : new ArrayList<>();
    }
    public List<UUID> getNeutrals(UUID player) { return api.getTeamMembers(player); }
    public List<UUID> getAllies(UUID player) { return getNeutrals(player); }
    public List<UUID> getTruces(UUID player) { return getNeutrals(player); }
    public List<UUID> getEnemies(UUID player) { return null; }

    public List<Player> getOnlineAssociates(UUID player) {
        final List<UUID> a = getAssociates(player);
        final List<Player> p = new ArrayList<>();
        for(UUID u : a) {
            final OfflinePlayer o = Bukkit.getOfflinePlayer(u);
            if(o != null && o.isOnline()) p.add(o.getPlayer());
        }
        return p;
    }

    public List<Chunk> getRegionalChunks(String regionalIdentifier) {
        final HashMap<UUID, Island> islands = api.getOwnedIslands();
        final World w = api.getIslandWorld();
        final List<Chunk> ch = new ArrayList<>();
        for(Island is : islands.values()) {
            final Location c = is.getCenter();
            final int x = c.getBlockX(), z = c.getBlockZ(), d = is.getIslandDistance();
            for(int i = x-d; i <= x+d; i++) {
                for(int o = z-d; o <= z+d; o++) {
                    final Chunk chunk = w.getChunkAt(new Location(w, x, 0, z));
                    if(!ch.contains(chunk)) ch.add(chunk);
                }
            }
        }
        return ch;
    }
    public String getRole(UUID player) {
        final Island i = api.getIslandOwnedBy(player);
        return i != null ? "ISLAND_OWNER" : "ISLAND_VISITOR";
    }
    public String getRegionalIdentifier(UUID player) { return api.getIslandName(player); }
    public String getRegionalIdentifierAt(Location l) {
        final Island i = api.getIslandAt(l);
        return i != null ? api.getIslandName(i.getOwner()) : null;
    }
    public String getChatMode(UUID player) { return "GLOBAL"; }
}
