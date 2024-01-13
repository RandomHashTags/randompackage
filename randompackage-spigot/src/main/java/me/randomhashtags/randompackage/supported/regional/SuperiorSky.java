package me.randomhashtags.randompackage.supported.regional;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import me.randomhashtags.randompackage.supported.Regional;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public enum SuperiorSky implements RPFeatureSpigot, Regional {
    INSTANCE;

    private com.bgsoftware.superiorskyblock.api.SuperiorSkyblock ss;

    @NotNull
    @Override
    public String getIdentifier() {
        return "REGIONAL_SUPERIOR_SKYBLOCK";
    }
    @Override
    public void load() {
        ss = SuperiorSkyblockAPI.getSuperiorSkyblock();
    }
    @Override
    public void unload() {
    }

    @Nullable
    private Island getIsland(UUID player) {
        return getSPlayer(player).getIsland();
    }
    @Nullable
    private Island getIslandAt(Location l) {
        return SuperiorSkyblockAPI.getIslandAt(l);
    }
    private SuperiorPlayer getSPlayer(@NotNull UUID player) {
        return SuperiorSkyblockAPI.getPlayer(player);
    }
    private Set<UUID> getAllMembers(@NotNull Island island) {
        return island.getIslandMembers(true).stream().map(SuperiorPlayer::getUniqueId).collect(Collectors.toSet());
    }

    public boolean canModify(UUID player, Location location) {
        final Island i = getIslandAt(location);
        return i == null || getAllMembers(i).contains(player);
    }

    public boolean areEnemies(UUID player1, UUID player2) {
        return false;
    }
    public @NotNull Set<UUID> getAssociates(UUID player) {
        final Island i = getIsland(player);
        return i != null ? getAllMembers(i) : null;
    }
    public @NotNull Set<UUID> getNeutrals(UUID player) {
        final Set<UUID> a = new HashSet<>();
        final UUID tl = getSPlayer(player).getIslandLeader().getUniqueId();
        if(tl != null && !tl.equals(player)) {
            a.add(tl);
        }
        final Island i = getIsland(player);
        if(i != null) {
            a.addAll(getAllMembers(i));
        }
        return a;
    }
    public @NotNull Set<UUID> getAllies(UUID player) {
        return getNeutrals(player);
    }
    public @NotNull Set<UUID> getTruces(UUID player) {
        return getNeutrals(player);
    }
    public @NotNull Set<UUID> getEnemies(UUID player) {
        return null;
    }

    public @NotNull Set<Player> getOnlineAssociates(UUID player) {
        final Island i = getIsland(player);
        final Set<Player> a = new HashSet<>();
        if(i != null) {
            for(SuperiorPlayer u : i.getIslandMembers(true)) {
                a.add(u.asPlayer());
            }
        }
        return a;
    }

    public @NotNull List<Chunk> getRegionalChunks(String regionalIdentifier) {
        try {
            final UUID u = UUID.fromString(regionalIdentifier);
            final Island i = ss.getGrid().getIsland(u);
            return i.getAllChunks();
        } catch (Exception e) {
            throw new NullPointerException("Regional Identifier with UUID \"" + regionalIdentifier + "\" not found!");
        }
    }
    public String getRole(UUID player) {
        final PlayerRole r = getSPlayer(player).getPlayerRole();
        return r != null ? r.getName() : null;
    }
    public String getRegionalIdentifier(UUID player) {
        final Island i = getIsland(player);
        return i != null ? player.toString() : null;
    }
    public String getRegionalIdentifierAt(Location l) {
        final Island i = getIslandAt(l);
        return i != null ? i.getOwner().getUniqueId().toString() : null;
    }
    public String getChatMode(UUID player) {
        return "GLOBAL";
    }
}
