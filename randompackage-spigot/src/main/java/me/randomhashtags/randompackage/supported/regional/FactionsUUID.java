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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class FactionsUUID extends Reflect implements Regional {
    private static FactionsUUID instance;
    public static FactionsUUID getFactionsUUID() {
        if(instance == null) instance = new FactionsUUID();
        return instance;
    }

    private FPlayers fi;
    private Factions f;
    private Board b;
    private HashMap<String, HashMap<String, List<UUID>>> relations;
    private boolean isLegacy;

    public String getIdentifier() { return "REGIONAL_FACTIONS_UUID"; }
    public void load() {
        fi = FPlayers.getInstance();
        f = Factions.getInstance();
        b = Board.getInstance();
        relations = new HashMap<>();
        try {
            Class.forName("com.massivecraft.factions.struct.Relation");
            isLegacy = true;
        } catch (Exception ignored) {
        }
    }
    public void unload() {
    }

    private boolean isRelation(FPlayer fplayer, Faction f, String type) {
        final Class<? extends FPlayer> c = fplayer.getClass();
        String n = "";
        try {
            if(isLegacy) {
                n = ((com.massivecraft.factions.struct.Relation) c.getMethod("getRelationTo", RelationParticipator.class).invoke(fplayer, f)).name();
            } else {
                n = ((com.massivecraft.factions.perms.Relation) c.getMethod("getRelationTo", RelationParticipator.class).invoke(fplayer, f)).name();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n.equalsIgnoreCase(type);

    }

    public Faction getFaction(UUID player) {
        final FPlayer fp = getFPlayer(player);
        return fp != null ? fp.getFaction() : null;
    }

    private FPlayer getFPlayer(UUID uuid) { return fi.getByOfflinePlayer(Bukkit.getOfflinePlayer(uuid)); }
    public ChatColor getRelationColor(OfflinePlayer player, Player target) {
        final Faction f = getFaction(player.getUniqueId());
        return f != null ? f.getColorTo(getFPlayer(target.getUniqueId())) : null;
    }

    private List<UUID> getType(UUID player, String TYPE) {
        final Faction f = getFaction(player);
        final String faction = f != null ? f.getTag() : null;
        if(faction == null) return new ArrayList<>();
        if(!relations.containsKey(faction)) {
            relations.put(faction, new HashMap<>());
        }
        final HashMap<String, List<UUID>> list = relations.get(faction);
        if(list.containsKey(TYPE)) {
            return list.get(TYPE);
        } else {
            final boolean m = TYPE.equals("MEMBERS"), e = TYPE.equals("ENEMIES"), a = TYPE.equals("ALLIES"), t = TYPE.equals("TRUCES"), n = TYPE.equals("NEUTRAL");
            final List<UUID> members = new ArrayList<>();
            for(FPlayer fp : fi.getAllFPlayers()) {
                if(m && isRelation(fp, f, "MEMBER")
                        || e && isRelation(fp, f, "ENEMY")
                        || a && isRelation(fp, f, "ALLY")
                        || t && isRelation(fp, f, "TRUCE")
                        || n && isRelation(fp, f, "NEUTRAL")
                )
                    members.add(fp.getPlayer().getUniqueId());
            }
            list.put(TYPE, members);
            return members;
        }
    }
    public List<UUID> getAssociates(UUID player) { return getType(player, "MEMBERS"); }
    public List<UUID> getNeutrals(UUID player) { return getType(player, "NEUTRAL"); }
    public List<UUID> getAllies(UUID player) { return getType(player, "ALLIES"); }
    public List<UUID> getTruces(UUID player) { return getType(player, "TRUCES"); }
    public List<UUID> getEnemies(UUID player) { return getType(player, "ENEMIES"); }

    public boolean canModify(UUID player, Location blockLocation) {
        final Faction p = getFPlayer(player).getFaction(), f = b.getFactionAt(new FLocation(blockLocation));
        return f.isWilderness() || p != null && p.equals(f);
    }

    public List<Player> getOnlineAssociates(UUID player) {
        final Faction f = getFaction(player);
        return f != null ? f.getOnlinePlayers() : new ArrayList<>();
    }

    public List<Chunk> getRegionalChunks(String regionalIdentifier) {
        final Faction faction = f.getByTag(regionalIdentifier);
        final List<Chunk> a = new ArrayList<>();
        if(faction != null) {
            for(FLocation l : faction.getAllClaims()) {
                final Chunk c = l.getWorld().getChunkAt((int) l.getX(), (int) l.getZ());
                if(!a.contains(c)) a.add(c);
            }
        }
        return a;
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
        return ChatColor.stripColor(b.getFactionAt(new FLocation(l)).getTag());
    }
    public String getChatMode(UUID player) { return getFPlayer(player).getChatMode().name(); }

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
