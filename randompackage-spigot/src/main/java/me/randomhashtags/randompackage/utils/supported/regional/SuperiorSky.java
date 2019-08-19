package me.randomhashtags.randompackage.utils.supported.regional;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.supported.Regional;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SuperiorSky extends RPFeature implements Regional {
    private static SuperiorSky instance;
    public static SuperiorSky getSuperiorSkyblock() {
        if(instance == null) instance = new SuperiorSky();
        return instance;
    }

    private com.bgsoftware.superiorskyblock.api.SuperiorSkyblock ss;

    public String getIdentifier() { return "REGIONAL_SUPERIOR_SKYBLOCK"; }
    public void load() {
        ss = SuperiorSkyblockAPI.getSuperiorSkyblock();
    }
    public void unload() {
        ss = null;
        instance = null;
    }

    private Island getIsland(UUID player) { return getSPlayer(player).getIsland(); }
    private Island getIslandAt(Location l) { return SuperiorSkyblockAPI.getIslandAt(l); }
    private SuperiorPlayer getSPlayer(UUID player) { return SuperiorSkyblockAPI.getPlayer(player); }

    public boolean canModify(UUID player, Location location) {
        final Island i = getIslandAt(location);
        return i == null || i.getAllMembers().contains(player);
    }


    public boolean areEnemies(UUID player1, UUID player2) { return false; }
    public List<UUID> getAssociates(UUID player) {
        final Island i = getIsland(player);
        return i != null ? i.getAllMembers() : null;
    }
    public List<UUID> getNeutrals(UUID player) {
        final List<UUID> a = new ArrayList<>();
        final UUID tl = getSPlayer(player).getTeamLeader();
        if(tl != null && !tl.equals(player)) a.add(tl);
        final Island i = getIsland(player);
        if(i != null) a.addAll(i.getAllMembers());
        return a;
    }
    public List<UUID> getAllies(UUID player) { return getNeutrals(player); }
    public List<UUID> getTruces(UUID player) { return getNeutrals(player); }
    public List<UUID> getEnemies(UUID player) { return null; }

    public List<Player> getOnlineAssociates(UUID player) {
        final Island i = getIsland(player);
        final List<Player> a = new ArrayList<>();
        if(i != null) {
            for(UUID u : i.getMembers()) {
                a.add(Bukkit.getPlayer(u));
            }
        }
        return a;
    }

    public List<Chunk> getRegionalChunks(String regionalIdentifier) {
        try {
            final UUID u = UUID.fromString(regionalIdentifier);
            final Island i = ss.getGrid().getIsland(u);
            return i.getAllChunks();
        } catch(Exception e) {
            throw new NullPointerException("Regional Identifier with UUID \"" + regionalIdentifier + "\" not found!");
        }
    }
    public String getRole(UUID player) {
        final IslandRole r = getSPlayer(player).getIslandRole();
        return r != null ? r.name() : null;
    }
    public String getRegionalIdentifier(UUID player) {
        final Island i = getIsland(player);
        return i != null ? player.toString() : null;
    }
    public String getRegionalIdentifierAt(Location l) {
        final Island i = getIslandAt(l);
        return i != null ? i.getOwner().getUniqueId().toString() : null;
    }
    public String getChatMode(UUID player) { return "GLOBAL"; }
}
