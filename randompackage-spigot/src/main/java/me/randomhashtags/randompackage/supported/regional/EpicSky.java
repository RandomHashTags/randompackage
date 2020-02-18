package me.randomhashtags.randompackage.supported.regional;

import com.peaches.epicskyblock.EpicSkyblock;
import com.peaches.epicskyblock.Island;
import com.peaches.epicskyblock.IslandManager;
import com.peaches.epicskyblock.User;
import me.randomhashtags.randompackage.supported.Regional;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class EpicSky extends RPFeature implements Regional {
    private static EpicSky instance;
    public static EpicSky getEpicSkyblock() {
        if(instance == null) instance = new EpicSky();
        return instance;
    }

    private IslandManager im;

    public String getIdentifier() {
        return "REGIONAL_EPICSKYBLOCK";
    }
    public void load() {
        im = EpicSkyblock.getIslandManager();
    }
    public void unload() {
    }

    private User getUser(UUID player) {
        return User.getUser(Bukkit.getOfflinePlayer(player).getName());
    }

    public boolean canModify(UUID player, Location l) {
        final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        final Island is = im.getIslandViaLocation(l);
        return is.getMembers().contains(op.getName());
    }

    public List<UUID> getAssociates(UUID player) {
        final List<UUID> a = new ArrayList<>();
        final User u = getUser(player);
        if(u != null) {
            final Island i = u.getIsland();
            if(i != null) {
                for(String s : i.getMembers()) {
                    a.add(Bukkit.getOfflinePlayer(s).getUniqueId());
                }
            }
        }
        return a;
    }
    public List<UUID> getNeutrals(UUID player) { return getAssociates(player); }
    public List<UUID> getAllies(UUID player) { return getAssociates(player); }
    public List<UUID> getTruces(UUID player) { return getAssociates(player); }
    public List<UUID> getEnemies(UUID player) { return new ArrayList<>(); }

    public List<Player> getOnlineAssociates(UUID player) {
        final List<UUID> a = getAssociates(player);
        final List<Player> online = new ArrayList<>();
        for(UUID u : a) {
            final OfflinePlayer op = Bukkit.getOfflinePlayer(u);
            if(op.isOnline()) {
                online.add(op.getPlayer());
            }
        }
        return online;
    }

    public List<Chunk> getRegionalChunks(String regionalIdentifier) {
        return null;
    }
    public String getRole(UUID player) {
        return null; // latest public release doesn't have roles
        //final User u = User.getUser(Bukkit.getOfflinePlayer(player).getName());
        //return u != null ? u.role.name() : null;
    }
    public String getRegionalIdentifier(UUID player) {
        final User u = getUser(player);
        final Island i = u.getIsland();
        return i != null ? Integer.toString(i.getId()) : null;
    }
    public String getRegionalIdentifierAt(Location l) {
        final Island i = im.getIslandViaLocation(l);
        return i != null ? Integer.toString(i.getId()) : null;
    }
    public String getChatMode(UUID player) { return "GLOBAL"; }
}
