package me.randomhashtags.randompackage.utils.supported;

import me.randomhashtags.randompackage.utils.supported.plugins.ASkyblock;
import me.randomhashtags.randompackage.utils.supported.plugins.SuperiorSkyblock;
import me.randomhashtags.randompackage.utils.supported.plugins.factionsUUID;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionsAPI {

    private static FactionsAPI instance;
    public static FactionsAPI getFactionsAPI() {
        if(instance == null) instance = new FactionsAPI();
        return instance;
    }

    public String factions;

    public boolean relationIsEnemyOrNull(Player player1, Player player2) {
        if(factions == null)                  return true;
        else if(factions.contains("Factions"))return factionsUUID.getInstance().relationIsEnemyOrNull(player1, player2);
        else return true;
    }
    public boolean relationIsNeutral(Player player1, Player player2) {
        if(factions == null)                  return false;
        else if(factions.contains("Factions"))return factionsUUID.getInstance().relationIsNeutral(player1, player2);
        else return false;
    }
    public boolean relationIsAlly(Player player1, Player player2) {
        if(factions == null)                  return false;
        else if(factions.contains("Factions"))return factionsUUID.getInstance().relationIsAlly(player1, player2);
        else if(factions.equals("ASkyblock")) return ASkyblock.getASkyblock().areTeammates(player1.getUniqueId(), player2.getUniqueId());
        else return false;
    }
    public boolean relationIsTruce(Player player1, Player player2) {
        if(factions == null)                  return false;
        else if(factions.contains("Factions"))return factionsUUID.getInstance().relationIsTruce(player1, player2);
        else if(factions.equals("ASkyblock")) return ASkyblock.getASkyblock().areTeammates(player1.getUniqueId(), player2.getUniqueId());
        else return false;
    }
    public boolean relationIsMember(Player player1, Player player2) {
        if(factions == null)                  return false;
        else if(factions.contains("Factions"))return factionsUUID.getInstance().relationIsMember(player1, player2);
        else if(factions.equals("ASkyblock")) return ASkyblock.getASkyblock().areTeammates(player1.getUniqueId(), player2.getUniqueId());
        else return false;
    }
    public String getFaction(OfflinePlayer player) {
        if(factions == null)                   return null;
        else if(factions.contains("Factions")) return factionsUUID.getInstance().getFaction(player);
        else if(factions.equals("ASkyblock")) return ASkyblock.getASkyblock().getIslandName(player.getUniqueId());
        else return null;
    }
    public boolean canModify(Player player, Location location) {
        if(factions == null) return true;
        else if(factions.contains("Factions")) return factionsUUID.getInstance().canModify(player, location);
        else if(factions.equals("ASkyblock")) return ASkyblock.getASkyblock().canModify(player.getUniqueId(), location);
        else if(factions.equals("SuperiorSkyblock")) return SuperiorSkyblock.getSuperiorSkylock().canModify(player.getUniqueId(), location);
        else return true;
    }
    public boolean isNotWarZoneOrSafeZone(Location l) {
        if(factions == null) return true;
        else if(factions.contains("Factions")) return factionsUUID.getInstance().isNotWarZoneOrSafeZone(l);
        else if(factions.equals("ASkyblock")) return ASkyblock.getASkyblock().isNotWarZoneOrSafeZone(l);
        else return true;
    }
    public List<Chunk> getWarZoneChunks() {
        if(factions == null) return new ArrayList<>();
        else if(factions.contains("Factions")) return factionsUUID.getInstance().getWarzoneClaims();
        else return new ArrayList<>();
    }
    public boolean locationIsWarZone(Block block) {
        if(factions == null)                   return true;
        else if(factions.contains("Factions")) return factionsUUID.getInstance().locationIsWarZone(block);
        else return false;
    }
    public ChatColor getRelationColor(OfflinePlayer player, Player target) {
        if(factions == null) return ChatColor.WHITE;
        else if(factions.contains("Factions")) return factionsUUID.getInstance().getRelation(player, target);
        else return ChatColor.WHITE;
    }
    public String getRole(Player player) {
        if(factions == null) return null;
        else if(factions.contains("Factions")) return factionsUUID.getInstance().getPlayerRole(player);
        else if(factions.equals("SuperiorSkyblock")) return SuperiorSkyblock.getSuperiorSkylock().getRole(player);
        else return null;
    }
    public String getFactionAt(Location l) {
        if(factions == null) return null;
        else if(factions.contains("Factions")) return factionsUUID.getInstance().getFactionAt(l);
        else return null;
    }


    public void increasePowerBoost(String factionName, double by) {
        if(factions != null && factionName != null) {
            if(factions.contains("Factions")) factionsUUID.getInstance().increasePower(factionName, by);
        }
    }
    public void setPowerBoost(String factionName, double value) {
        if(factions != null) {
            if(factions.contains("Factions")) factionsUUID.getInstance().setPowerBoost(factionName, value);
        }
    }
    public void resetPowerBoost(Player player) {
        if(factions != null) {
            if(factions.contains("Factions")) factionsUUID.getInstance().resetPowerBoost(player);
        }
    }

}
