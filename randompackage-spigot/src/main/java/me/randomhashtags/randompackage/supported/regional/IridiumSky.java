package me.randomhashtags.randompackage.supported.regional;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import com.iridium.iridiumteams.database.IridiumUser;
import me.randomhashtags.randompackage.supported.Regional;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public enum IridiumSky implements RPFeatureSpigot, Regional {
    INSTANCE;

    @Override
    public @NotNull String getIdentifier() {
        return "REGIONAL_IRIDIUM_SKYBLOCK";
    }
    @Override
    public void load() {
    }
    @Override
    public void unload() {
    }

    @NotNull
    private User getUser(UUID player) {
        return IridiumSkyblockAPI.getInstance().getUser(Bukkit.getOfflinePlayer(player));
    }

    public boolean canModify(UUID player, Location l) {
        final User user = getUser(player);
        final Optional<Island> is = IridiumSkyblockAPI.getInstance().getIslandViaLocation(l);
        return is.isPresent() && IridiumSkyblock.getInstance().getIslandManager().getMembersOnIsland(is.get()).contains(user);
    }
    @NotNull
    public List<UUID> getAssociates(UUID player) {
        final Optional<Island> island = getUser(player).getIsland();
        if(island.isPresent()) {
            return IridiumSkyblock.getInstance().getIslandManager().getMembersOnIsland(island.get()).stream().map(IridiumUser::getUuid).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
    public @NotNull List<UUID> getNeutrals(UUID player) {
        return getAssociates(player);
    }
    public @NotNull List<UUID> getAllies(UUID player) {
        return getAssociates(player);
    }
    public @NotNull List<UUID> getTruces(UUID player) {
        return getAssociates(player);
    }
    public @NotNull List<UUID> getEnemies(UUID player) {
        return new ArrayList<>();
    }

    public @NotNull List<Player> getOnlineAssociates(UUID player) {
        final List<UUID> associates = getAssociates(player);
        final List<Player> online = new ArrayList<>();
        for(UUID u : associates) {
            final OfflinePlayer op = Bukkit.getOfflinePlayer(u);
            if(op.isOnline()) {
                online.add(op.getPlayer());
            }
        }
        return online;
    }

    public @NotNull List<Chunk> getRegionalChunks(String regionalIdentifier) {
        return new ArrayList<>();
    }
    public String getRole(UUID player) {
        return null; // latest public release doesn't have roles
        //final User u = User.getUser(Bukkit.getOfflinePlayer(player).getName());
        //return u != null ? u.role.name() : null;
    }
    public String getRegionalIdentifier(UUID player) {
        final User u = getUser(player);
        final Optional<Island> i = u.getIsland();
        return i.isPresent() ? Integer.toString(i.get().getId()) : null;
    }
    public String getRegionalIdentifierAt(Location l) {
        final Optional<Island> i = IridiumSkyblockAPI.getInstance().getIslandViaLocation(l);
        return i.isPresent() ? Integer.toString(i.get().getId()) : null;
    }
    public String getChatMode(UUID player) {
        return "GLOBAL";
    }
}
