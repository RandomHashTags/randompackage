package me.randomhashtags.randompackage.supported.regional;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.iface.RelationParticipator;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum FactionsUUID implements Reflect, Regional {
    INSTANCE;

    private FPlayers fplayers;
    private Factions factions;
    private Board board;
    private HashMap<String, HashMap<String, List<UUID>>> relations;
    private boolean isLegacy;

    @NotNull
    @Override
    public String getIdentifier() {
        return "REGIONAL_FACTIONS_UUID";
    }
    @Override
    public void load() {
        fplayers = FPlayers.getInstance();
        factions = Factions.getInstance();
        board = Board.getInstance();
        relations = new HashMap<>();
        try {
            Class.forName("com.massivecraft.factions.struct.Relation");
            isLegacy = true;
        } catch (Exception ignored) {
        }
    }
    @Override
    public void unload() {
    }

    private boolean isRelation(FPlayer fplayer, Faction faction, String type) {
        final Class<? extends FPlayer> targetClass = fplayer.getClass();
        String relation = "";
        try {
            if(isLegacy) {
                relation = ((com.massivecraft.factions.struct.Relation) targetClass.getMethod("getRelationTo", RelationParticipator.class).invoke(fplayer, faction)).name();
            } else {
                relation = ((com.massivecraft.factions.perms.Relation) targetClass.getMethod("getRelationTo", RelationParticipator.class).invoke(fplayer, faction)).name();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return relation.equalsIgnoreCase(type);

    }

    public Faction getFaction(UUID player) {
        final FPlayer fp = getFPlayer(player);
        return fp != null ? fp.getFaction() : null;
    }

    private FPlayer getFPlayer(UUID uuid) {
        return fplayers.getByOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
    }
    public ChatColor getRelationColor(OfflinePlayer player, Player target) {
        final Faction f = getFaction(player.getUniqueId());
        return f != null ? f.getColorTo(getFPlayer(target.getUniqueId())) : null;
    }

    private List<UUID> getType(UUID player, String TYPE) {
        final Faction f = getFaction(player);
        final String faction = f != null ? f.getTag() : null;
        if(faction == null) {
            return new ArrayList<>();
        }
        relations.putIfAbsent(faction, new HashMap<>());
        final HashMap<String, List<UUID>> list = relations.get(faction);
        if(list.containsKey(TYPE)) {
            return list.get(TYPE);
        } else {
            final boolean isMembers = TYPE.equals("MEMBERS"), isEnemies = TYPE.equals("ENEMIES"), isAllies = TYPE.equals("ALLIES"), isTruces = TYPE.equals("TRUCES"), isNeutral = TYPE.equals("NEUTRAL");
            final List<UUID> members = new ArrayList<>();
            for(FPlayer fp : fplayers.getAllFPlayers()) {
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
            list.put(TYPE, members);
            return members;
        }
    }
    @Override
    public List<UUID> getAssociates(UUID player) {
        return getType(player, "MEMBERS");
    }
    @Override
    public List<UUID> getNeutrals(UUID player) {
        return getType(player, "NEUTRAL");
    }
    @Override
    public List<UUID> getAllies(UUID player) {
        return getType(player, "ALLIES");
    }
    @Override
    public List<UUID> getTruces(UUID player) {
        return getType(player, "TRUCES");
    }
    @Override
    public List<UUID> getEnemies(UUID player) {
        return getType(player, "ENEMIES");
    }

    public boolean canModify(UUID player, Location blockLocation) {
        final Faction p = getFPlayer(player).getFaction(), f = board.getFactionAt(new FLocation(blockLocation));
        return f.isWilderness() || p != null && p.equals(f);
    }

    public List<Player> getOnlineAssociates(UUID player) {
        final Faction f = getFaction(player);
        return f != null ? f.getOnlinePlayers() : new ArrayList<>();
    }

    public List<Chunk> getRegionalChunks(String regionalIdentifier) {
        final Faction faction = factions.getByTag(regionalIdentifier);
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

    public String getRole(UUID player) {
        if(isLegacy) {
            sendConsoleMessage("&6[RandomPackage] &cERROR. Make sure you're using a supported FactionsUUID version! Messages sent may appear inaccurate to others!");
            return "";
        }
        return getFPlayer(player).getRole().getPrefix();
    }
    public String getRegionalIdentifier(UUID player) {
        final Faction f = getFaction(player);
        return f != null ? f.getTag() : null;
    }
    public String getRegionalIdentifierAt(Location l) {
        return ChatColor.stripColor(board.getFactionAt(new FLocation(l)).getTag());
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
