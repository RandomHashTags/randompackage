package me.randomhashtags.randompackage.utils.supported;

import me.randomhashtags.randompackage.utils.classes.customenchants.RarityGem;
import me.randomhashtags.randompackage.utils.supported.plugins.ASkyblock;
import me.randomhashtags.randompackage.utils.supported.plugins.factionsUUID;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FactionsAPI {

    private static FactionsAPI instance;
    public static FactionsAPI getFactionsAPI() {
        if(instance == null) instance = new FactionsAPI();
        return instance;
    }

    public String factions;
    public HashMap<String, Double> teleportDelayMultipliers = new HashMap<>(), cropGrowthMultipliers = new HashMap<>(), enemyDamageMultipliers = new HashMap<>(), bossDamageMultipliers = new HashMap<>(), vkitLevelingChances = new HashMap<>();
    public HashMap<String, HashMap<RarityGem, Double>> decreaseRarityGemCost = new HashMap<>();

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
    public String getFaction(Player player) {
        if(factions == null)                   return null;
        else if(factions.contains("Factions")) return factionsUUID.getInstance().getFaction(player);
        else if(factions.equals("ASkyblock")) return ASkyblock.getASkyblock().getIslandName(player.getUniqueId());
        else return null;
    }
    public boolean canBreakBlock(Player player, Location blockLocation) {
        if(factions == null) return true;
        else if(factions.contains("Factions")) return factionsUUID.getInstance().canBreakBlock(player, blockLocation);
        else if(factions.equals("ASkyblock")) return ASkyblock.getASkyblock().canBreakBlock(player.getUniqueId(), blockLocation);
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
    public ChatColor getRelationColor(Player player, Player target) {
        if(factions == null) return ChatColor.WHITE;
        else if(factions.contains("Factions")) return factionsUUID.getInstance().getRelation(player, target);
        else return ChatColor.WHITE;
    }
    public String getRole(Player player) {
        if(factions == null) return null;
        else if(factions.contains("Factions")) return factionsUUID.getInstance().getPlayerRole(player);
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

    public double getTeleportDelayMultiplier(String factionName) {
        if(factions == null) return 1.00;
        else if(factions.contains("Factions")) return teleportDelayMultipliers.getOrDefault(factionName, 1.00);
        else return 1.00;
    }
    public void setTeleportDelayMultiplier(String factionName, double multiplier) {
        teleportDelayMultipliers.put(factionName, multiplier);
    }
    public void resetTeleportDelayMultiplier(String factionName) {
        if(factionName != null) {
            teleportDelayMultipliers.put(factionName, 1.00);
        }
    }

    public double getCropGrowthMultiplier(String factionName) {
        if(factions == null || factionName == null) return 1.00;
        else if(factions.contains("Factions")) return cropGrowthMultipliers.getOrDefault(factionName, 1.00);
        else return 1.00;
    }
    public void setCropGrowthMultiplier(String factionName, double multiplier) {
        if(factions != null && factionName != null) {
            cropGrowthMultipliers.put(factionName, multiplier);
        }
    }
    public double getBossDamageMultiplier(String factionName) {
        return factionName != null ? bossDamageMultipliers.getOrDefault(factionName, 1.00) : 1.00;
    }
    public void setBossDamageMultiplier(String factionName, double multiplier) {
        if(factionName != null) {
            bossDamageMultipliers.put(factionName, multiplier);
        }
    }
    public double getEnemyDamageMultiplier(String factionName) {
        final HashMap<String, Double> e = enemyDamageMultipliers;
        return factionName != null ? e.getOrDefault(factionName, 1.00) : 1.00;
    }
    public void setEnemyDamageMultiplier(String factionName, double multiplier) {
        if(factionName != null) enemyDamageMultipliers.put(factionName, multiplier);
    }

    public double getDecreaseRarityGemPercent(String factionName, RarityGem gem) {
        final HashMap<String, HashMap<RarityGem, Double>> d = decreaseRarityGemCost;
        return factionName != null && gem != null && d.containsKey(factionName) ? d.get(factionName).getOrDefault(gem, 0.00) : 0;
    }
    public double getVkitLevelingChance(String factionName) {
        return factionName != null ? vkitLevelingChances.getOrDefault(factionName, 0.00) : 0.00;
    }
    public void setVkitLevelingChance(String factionName, double chance) {
        if(factionName != null) {
            vkitLevelingChances.put(factionName, chance);
        }
    }
}
