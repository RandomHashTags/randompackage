package me.randomhashtags.randompackage.dev.factions;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface Faction {

    void load();
    void unload();

    void backup();

    @NotNull UUID getUUID();
    long getCreationTime();
    @Nullable Location getHome();
    @NotNull String getTag();
    @Nullable String getDescription();
    @Nullable List<Chunk> getClaims();
    @NotNull HashMap<FPlayer, FactionRole> getMembers();
    @NotNull FactionBank getBank();
    @Nullable HashMap<Relation, FactionWarp> getWarps();

    @NotNull HashMap<String, Boolean> getFactionSettings();
    default boolean getFactionSetting(@NotNull String identifier) {
        return getFactionSettings().getOrDefault(identifier, false);
    }
    default void setFactionSetting(@NotNull String identifier, boolean enabled) {
        getFactionSettings().put(identifier, enabled);
    }
    default boolean isOpen() {
        return getFactionSetting("OPEN");
    }
    default void setOpen(boolean open) {
        setFactionSetting("OPEN", open);
    }

    @NotNull HashMap<FPlayer, HashMap<String, Boolean>> getPlayerSettings();
    default boolean getPlayerSetting(@NotNull FPlayer player, @NotNull String identifier) {
        return getPlayerSettings().get(player).getOrDefault(identifier, false);
    }
    default void setPlayerSetting(@NotNull FPlayer player, @NotNull String identifier, boolean enabled) {
        final HashMap<FPlayer, HashMap<String, Boolean>> settings = getPlayerSettings();
        if(!settings.containsKey(player)) {
            settings.put(player, new HashMap<>());
        }
        settings.get(player).put(identifier, enabled);
    }
    default boolean inChatMode(@NotNull FPlayer player, @NotNull FactionChatMode mode) {
        return getPlayerSetting(player, mode.getIdentifier());
    }

    @Nullable List<UUID> getBanned();
    @Nullable HashMap<UUID, Relationship> getRelations();
}
