package me.randomhashtags.randompackage.supported;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface Regional {
    default boolean list_contains_player(Collection<UUID> list, UUID player2) {
        return list != null && list.contains(player2);
    }
    default boolean isAssociate(UUID player1, UUID player2) {
        return list_contains_player(getAssociates(player1), player2);
    }
    default boolean isAssociate(OfflinePlayer player1, OfflinePlayer player2) {
        return isAssociate(player1.getUniqueId(), player2.getUniqueId());
    }
    default boolean isNeutral(UUID player1, UUID player2) {
        return list_contains_player(getNeutrals(player1), player2);
    }
    default boolean isNeutral(OfflinePlayer player1, OfflinePlayer player2) {
        return isNeutral(player1.getUniqueId(), player2.getUniqueId());
    }
    default boolean isAlly(UUID player1, UUID player2) {
        return list_contains_player(getAllies(player1), player2);
    }
    default boolean isAlly(OfflinePlayer player1, OfflinePlayer player2) {
        return isAlly(player1.getUniqueId(), player2.getUniqueId()); }
    default boolean isTruce(UUID player1, UUID player2) {
        return list_contains_player(getTruces(player1), player2);
    }
    default boolean isTruce(OfflinePlayer player1, OfflinePlayer player2) {
        return isTruce(player1.getUniqueId(), player2.getUniqueId());
    }
    default boolean isEnemy(UUID player1, UUID player2) {
        return list_contains_player(getEnemies(player1), player2);
    }
    default boolean isEnemy(OfflinePlayer player1, OfflinePlayer player2) {
        return isEnemy(player1.getUniqueId(), player2.getUniqueId());
    }

    default void sendMessageToServer(BaseComponent...message) {
        Bukkit.getServer().spigot().broadcast(message);
    }
    default void sendMessageTo(Collection<UUID> players, BaseComponent...message) {
        if(players != null) {
            for(UUID u : players) {
                final OfflinePlayer o = Bukkit.getOfflinePlayer(u);
                if(o.isOnline()) {
                    o.getPlayer().spigot().sendMessage(message);
                }
            }
        }
    }
    default void sendMessageToAssociates(UUID player, BaseComponent...message) {
        sendMessageTo(getAssociates(player), message);
    }
    default void sendMessageToAllies(UUID player, BaseComponent...message) {
        sendMessageTo(getAllies(player), message);
    }
    default void sendMessageToTruces(UUID player, BaseComponent...message) {
        sendMessageTo(getTruces(player), message);
    }
    default void sendMessageToEnemies(UUID player, BaseComponent...message) {
        sendMessageTo(getEnemies(player), message);
    }

    boolean canModify(UUID player, Location location);

    default Collection<UUID> getAssociates(OfflinePlayer player) {
        return getAssociates(player.getUniqueId());
    }
    default Collection<UUID> getNeutrals(OfflinePlayer player) {
        return getNeutrals(player.getUniqueId());
    }
    default Collection<UUID> getAllies(OfflinePlayer player) {
        return getAllies(player.getUniqueId());
    }
    default Collection<UUID> getTruces(OfflinePlayer player) {
        return getTruces(player.getUniqueId());
    }
    default Collection<UUID> getEnemies(OfflinePlayer player) {
        return getEnemies(player.getUniqueId());
    }


    @NotNull Collection<UUID> getAssociates(UUID player);
    @NotNull Collection<UUID> getNeutrals(UUID player);
    @NotNull Collection<UUID> getAllies(UUID player);
    @NotNull Collection<UUID> getTruces(UUID player);
    @NotNull Collection<UUID> getEnemies(UUID player);

    @NotNull Collection<Player> getOnlineAssociates(UUID player);
    @NotNull Collection<Chunk> getRegionalChunks(String regionalIdentfier);
    String getRole(UUID uuid);
    String getRegionalIdentifier(UUID player);
    String getRegionalIdentifierAt(Location l);
    String getChatMode(UUID player);
}
