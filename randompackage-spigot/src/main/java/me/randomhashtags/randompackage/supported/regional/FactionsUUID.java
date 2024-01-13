package me.randomhashtags.randompackage.supported.regional;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.event.LandClaimEvent;
import me.randomhashtags.randompackage.event.regional.FactionClaimLandEvent;
import me.randomhashtags.randompackage.event.regional.FactionLeaveEvent;
import me.randomhashtags.randompackage.event.regional.RegionDisbandEvent;
import me.randomhashtags.randompackage.event.regional.RegionRenameEvent;
import me.randomhashtags.randompackage.supported.Regional;
import me.randomhashtags.randompackage.util.Reflect;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum FactionsUUID implements Reflect, Regional {
    INSTANCE;
    private HashMap<String, HashMap<String, List<UUID>>> relations;

    @NotNull
    @Override
    public String getIdentifier() {
        return "REGIONAL_FACTIONS_UUID";
    }
    @Override
    public void load() {
        relations = new HashMap<>();
    }
    @Override
    public void unload() {
    }

    private boolean isRelation(FPlayer fplayer, Faction faction, String type) {
        return fplayer.getRelationTo(faction).name().equalsIgnoreCase(type);

    }

    @Nullable
    public Faction getFaction(@NotNull UUID player) {
        final FPlayer fp = getFPlayer(player);
        return fp != null ? fp.getFaction() : null;
    }

    private FPlayer getFPlayer(@NotNull UUID uuid) {
        return FPlayers.getInstance().getByOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
    }
    @Nullable
    public ChatColor getRelationColor(@NotNull OfflinePlayer player, @NotNull Player target) {
        final Faction f = getFaction(player.getUniqueId());
        return f != null ? f.getColorTo(getFPlayer(target.getUniqueId())) : null;
    }

    @NotNull
    private List<UUID> getType(@NotNull UUID player, @NotNull String relation_type) {
        final Faction f = getFaction(player);
        final String faction = f != null ? f.getTag() : null;
        if(faction == null) {
            return new ArrayList<>();
        }
        relations.putIfAbsent(faction, new HashMap<>());
        final HashMap<String, List<UUID>> list = relations.get(faction);
        if(list.containsKey(relation_type)) {
            return list.get(relation_type);
        } else {
            final boolean isMembers = relation_type.equals("MEMBERS"), isEnemies = relation_type.equals("ENEMIES"), isAllies = relation_type.equals("ALLIES"), isTruces = relation_type.equals("TRUCES"), isNeutral = relation_type.equals("NEUTRAL");
            final List<UUID> members = new ArrayList<>();
            for(FPlayer fp : FPlayers.getInstance().getAllFPlayers()) {
                if(fp != null && (
                        isMembers && isRelation(fp, f, "MEMBER")
                        || isEnemies && isRelation(fp, f, "ENEMY")
                        || isAllies && isRelation(fp, f, "ALLY")
                        || isTruces && isRelation(fp, f, "TRUCE")
                        || isNeutral && isRelation(fp, f, "NEUTRAL"))) {
                    final Player target = fp.getPlayer();
                    if(target != null) {
                        members.add(target.getUniqueId());
                    }
                }
            }
            list.put(relation_type, members);
            return members;
        }
    }
    @Override
    public @NotNull List<UUID> getAssociates(UUID player) {
        return getType(player, "MEMBERS");
    }
    @Override
    public @NotNull List<UUID> getNeutrals(UUID player) {
        return getType(player, "NEUTRAL");
    }
    @Override
    public @NotNull List<UUID> getAllies(UUID player) {
        return getType(player, "ALLIES");
    }
    @Override
    public @NotNull List<UUID> getTruces(UUID player) {
        return getType(player, "TRUCES");
    }
    @Override
    public @NotNull List<UUID> getEnemies(UUID player) {
        return getType(player, "ENEMIES");
    }

    public boolean canModify(@NotNull UUID player, Location blockLocation) {
        final Faction p = getFPlayer(player).getFaction(), f = Board.getInstance().getFactionAt(new FLocation(blockLocation));
        return f.isWilderness() || p != null && p.equals(f);
    }

    @NotNull
    public List<Player> getOnlineAssociates(@NotNull UUID player) {
        final Faction f = getFaction(player);
        return f != null ? f.getOnlinePlayers() : new ArrayList<>();
    }

    @NotNull
    public List<Chunk> getRegionalChunks(String regionalIdentifier) {
        final Faction faction = Factions.getInstance().getByTag(regionalIdentifier);
        final List<Chunk> chunks = new ArrayList<>();
        if(faction != null) {
            for(FLocation location : faction.getAllClaims()) {
                final Chunk chunk = location.getWorld().getChunkAt((int) location.getX(), (int) location.getZ());
                if(!chunks.contains(chunk)) {
                    chunks.add(chunk);
                }
            }
        }
        return chunks;
    }

    public String getRole(@NotNull UUID player) {
        return getFPlayer(player).getRole().getPrefix();
    }
    @Nullable
    public String getRegionalIdentifier(UUID player) {
        final Faction f = getFaction(player);
        return f != null ? f.getTag() : null;
    }
    public String getRegionalIdentifierAt(Location l) {
        return ChatColor.stripColor(Board.getInstance().getFactionAt(new FLocation(l)).getTag());
    }
    public String getChatMode(UUID player) {
        return getFPlayer(player).getChatMode().name();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void factionDisbandEvent(FactionDisbandEvent event) {
        PLUGIN_MANAGER.callEvent(new RegionDisbandEvent(event.getPlayer(), event.getFaction().getTag()));
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void factionRenameEvent(FactionRenameEvent event) {
        PLUGIN_MANAGER.callEvent(new RegionRenameEvent(event.getPlayer(), event.getOldFactionTag(), event.getFactionTag()));
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void factionLeaveEvent(FPlayerLeaveEvent event) {
        PLUGIN_MANAGER.callEvent(new FactionLeaveEvent(event.getfPlayer().getPlayer(), event.getFaction().getTag()));
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void factionClaimEvent(LandClaimEvent event) {
        PLUGIN_MANAGER.callEvent(new FactionClaimLandEvent(event.getfPlayer().getPlayer(), event.getFaction().getTag(), event.getLocation().getChunk()));
    }
}
