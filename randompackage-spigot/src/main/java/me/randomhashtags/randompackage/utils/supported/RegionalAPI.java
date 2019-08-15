package me.randomhashtags.randompackage.utils.supported;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.api.nearFinished.FactionUpgrades;
import me.randomhashtags.randompackage.utils.supported.regional.ASkyblock;
import me.randomhashtags.randompackage.utils.supported.regional.FactionsUUID;
import me.randomhashtags.randompackage.utils.supported.regional.SuperiorSkyblock;
import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegionalAPI extends UVersion {
    private static RegionalAPI instance;
    public static RegionalAPI getRegionalAPI() {
        if(instance == null) instance = new RegionalAPI();
        return instance;
    }

    private FileConfiguration config;
    private static boolean factionsUUID, askyblock, superiorskyblock;

    protected static FactionsUUID factions;
    protected static ASkyblock asky;
    protected static SuperiorSkyblock ssky;

    private boolean isTrue(String path) { return config.getBoolean(path); }
    public void setup(RandomPackage randompackage) {
        this.config = randompackage.config;
        trySupportingFactions();
        trySupportingASkyblock();
        trySupportingSuperiorSkyblock();
    }
    public void trySupportingFactions() {
        factionsUUID = isTrue( "supported plugins.regional.FactionsUUID") && pluginmanager.isPluginEnabled("Factions");
        if(factionsUUID) {
            factions = FactionsUUID.getFactionsUUID();
            factions.enable();
            hooked("FactionsUUID");
            FactionUpgrades.getFactionUpgrades().enable();
        }
    }
    public void trySupportingASkyblock() {
        askyblock = isTrue("supported plugins.regional.ASkyblock") && pluginmanager.isPluginEnabled("ASkyblock");
        if(askyblock) {
            asky = ASkyblock.getASkyblock();
            asky.enable();
            hooked("ASkyblock");
        }
    }
    public void trySupportingSuperiorSkyblock() {
        superiorskyblock = isTrue("supported plugins.regional.SuperiorSkyblock") && pluginmanager.isPluginEnabled("SuperiorSkyblock");
        if(superiorskyblock) {
            ssky = SuperiorSkyblock.getSuperiorSkyblock();
            ssky.enable();
            hooked("SuperiorSkyblock");
        }
    }
    private void hooked(String plugin) { sendConsoleMessage("&6[RandomPackage] &aHooked Regional Plugin: " + plugin); }
    public boolean hookedFactionsUUID() { return factionsUUID; }
    public boolean hookedASkyblock() { return askyblock; }
    public boolean hookedSuperiorSkyblock() { return superiorskyblock; }

    private List<UUID> getRelation(UUID player, int type) {
        final List<UUID> a = new ArrayList<>();
        if(factions != null) add(player, type, factions, a);
        if(asky != null) add(player, type, asky, a);
        if(ssky != null) add(player, type, ssky, a);
        return a;
    }
    private void add(UUID player, int type, Regional plugin, List<UUID> list) {
        List<UUID> l = null;
        switch(type) {
            case 0:
                l = plugin.getAssociates(player);
                if(l != null) list.addAll(l);
                return;
            case 1:
                l = plugin.getNeutrals(player);
                if(l != null) list.addAll(l);
                break;
            case 2:
                l = plugin.getAllies(player);
                if(l != null) list.addAll(l);
                break;
            case 3:
                l = plugin.getTruces(player);
                if(l != null) list.addAll(l);
                break;
            case 4:
                l = plugin.getEnemies(player);
                if(l != null) list.addAll(l);
                break;
            default:
                break;
        }
    }

    public List<UUID> getAssociates(UUID player) { return getRelation(player, 0); }
    public List<UUID> getNeutrals(UUID player) { return getRelation(player, 1); }
    public List<UUID> getAllies(UUID player) { return getRelation(player, 2); }
    public List<UUID> getTruces(UUID player) { return getRelation(player, 3); }
    public List<UUID> getEnemies(UUID player) { return getRelation(player, 4); }

    public String getFactionTagAt(Location l) { return factionsUUID ? factions.getRegionalIdentifierAt(l) : null; }
    public String getFactionTag(UUID player) { return factionsUUID ? factions.getRegionalIdentifier(player) : null; }
    public String getFactionTag(OfflinePlayer player) { return getFactionTag(player.getUniqueId()); }
    public List<UUID> getFactionMembers(UUID player) { return factionsUUID ? factions.getAssociates(player) : null; }

    public List<Player> getOnlineAssociates(UUID player, Regional plugin) { return plugin.getOnlineAssociates(player); }
    public List<Chunk> getChunks(String regionalIdentifier, Regional plugin) { return plugin.getChunks(regionalIdentifier); }
    public String getRole(UUID player, Regional plugin) { return plugin.getRole(player); }
    public String getRegionalIdentifier(UUID player, Regional plugin) { return plugin.getRegionalIdentifier(player); }
    public String getRegionalIdentifier(Location l, Regional plugin) { return plugin.getRegionalIdentifierAt(l); }
    public String getChatMode(UUID player, Regional plugin) { return plugin.getChatMode(player); }
}
