package me.randomhashtags.randompackage.supported.regional;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.supported.Regional;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum ASky implements RPFeatureSpigot, Regional {
    INSTANCE;

    @Override
    public @NotNull String getIdentifier() {
        return "REGIONAL_ASKYBLOCK";
    }
    @Override
    public void load() {
    }
    @Override
    public void unload() {
    }

    private boolean areTeammates(UUID player1, UUID player2) {
        final ASkyBlockAPI api = ASkyBlockAPI.getInstance();
        return api.inTeam(player1) && api.getTeamMembers(player1).contains(player2);
    }

    public boolean canModify(UUID player, Location location) {
        final Island i = ASkyBlockAPI.getInstance().getIslandAt(location);
        return i != null && (i.getMembers().contains(player) || areTeammates(i.getOwner(), player));
    }

    public @NotNull List<UUID> getAssociates(UUID player) {
        final Island is = ASkyBlockAPI.getInstance().getIslandOwnedBy(player);
        return is != null ? is.getMembers() : new ArrayList<>();
    }
    public @NotNull List<UUID> getNeutrals(UUID player) {
        return ASkyBlockAPI.getInstance().getTeamMembers(player);
    }
    public @NotNull List<UUID> getAllies(UUID player) {
        return getNeutrals(player);
    }
    public @NotNull List<UUID> getTruces(UUID player) {
        return getNeutrals(player);
    }
    public @NotNull List<UUID> getEnemies(UUID player) {
        return List.of();
    }

    public @NotNull List<Player> getOnlineAssociates(UUID player) {
        final List<UUID> a = getAssociates(player);
        final List<Player> p = new ArrayList<>();
        for(UUID u : a) {
            final OfflinePlayer o = Bukkit.getOfflinePlayer(u);
            if(o != null && o.isOnline()) {
                p.add(o.getPlayer());
            }
        }
        return p;
    }

    public @NotNull List<Chunk> getRegionalChunks(String regionalIdentifier) {
        final ASkyBlockAPI api = ASkyBlockAPI.getInstance();
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
    @NotNull
    public String getRole(UUID player) {
        final Island i = ASkyBlockAPI.getInstance().getIslandOwnedBy(player);
        return i != null ? "ISLAND_OWNER" : "ISLAND_VISITOR";
    }
    public String getRegionalIdentifier(UUID player) {
        return ASkyBlockAPI.getInstance().getIslandName(player);
    }
    @Nullable
    public String getRegionalIdentifierAt(Location l) {
        final ASkyBlockAPI api = ASkyBlockAPI.getInstance();
        final Island i = api.getIslandAt(l);
        return i != null ? api.getIslandName(i.getOwner()) : null;
    }
    @NotNull
    public String getChatMode(UUID player) {
        return "GLOBAL";
    }
}
