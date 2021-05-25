package me.randomhashtags.randompackage.supported;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.api.FactionUpgrades;
import me.randomhashtags.randompackage.attribute.faction.AddFactionPower;
import me.randomhashtags.randompackage.supported.regional.*;
import me.randomhashtags.randompackage.universal.UVersionable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum RegionalAPI implements UVersionable {
    INSTANCE;

    private FileConfiguration config;
    private static boolean WORLD_GUARD, factionsUUID, askyblock, superiorskyblock, epicskyblock;

    protected static FactionsUUID FACTIONS_UUID;
    protected static ASky A_SKY;
    protected static SuperiorSky SUPERIOR_SKY;
    protected static IridiumSky IRIDIUM_SKY;

    private boolean isTrue(String path) {
        return config.getBoolean(path);
    }
    public void setup(RandomPackage randompackage) {
        this.config = randompackage.config;
        trySupportingWorldGuard();
        trySupportingFactions();
        trySupportingASkyblock();
        trySupportingSuperiorSkyblock();
        trySupportingEpicSkyblock();
    }

    public void trySupportingWorldGuard() {
        WORLD_GUARD = isTrue("supported plugins.regional.WorldGuard") && PLUGIN_MANAGER.isPluginEnabled("WorldGuard");
        if(WORLD_GUARD) {
            hooked("WorldGuard");
        }
    }
    public void trySupportingFactions() {
        factionsUUID = isTrue("supported plugins.regional.FactionsUUID") && PLUGIN_MANAGER.isPluginEnabled("Factions");
        if(factionsUUID) {
            FACTIONS_UUID = FactionsUUID.INSTANCE;
            FACTIONS_UUID.enable();
            hooked("FactionsUUID");
            if(RP_CONFIG.getBoolean("faction upgrades.enabled")) {
                FactionUpgrades.getFactionUpgrades().enable();
                new AddFactionPower().load();
            }
        }
    }
    public void trySupportingASkyblock() {
        askyblock = isTrue("supported plugins.regional.ASkyblock") && PLUGIN_MANAGER.isPluginEnabled("ASkyblock");
        if(askyblock) {
            A_SKY = ASky.INSTANCE;
            A_SKY.enable();
            hooked("ASkyblock");
        }
    }
    public void trySupportingSuperiorSkyblock() {
        superiorskyblock = isTrue("supported plugins.regional.SuperiorSkyblock") && PLUGIN_MANAGER.isPluginEnabled("SuperiorSkyblock");
        if(superiorskyblock) {
            SUPERIOR_SKY = SuperiorSky.INSTANCE;
            SUPERIOR_SKY.enable();
            hooked("SuperiorSkyblock");
        }
    }
    public void trySupportingEpicSkyblock() {
        epicskyblock = isTrue("supported plugins.regional.EpicSkyblock") && PLUGIN_MANAGER.isPluginEnabled("EpicSkyblock");
        if(epicskyblock) {
            IRIDIUM_SKY = IridiumSky.INSTANCE;
            IRIDIUM_SKY.enable();
            hooked("EpicSkyblock");
        }
    }
    private void hooked(String plugin) {
        sendConsoleMessage("&6[RandomPackage] &aHooked Regional Plugin: " + plugin);
    }
    public boolean hookedFactionsUUID() {
        return factionsUUID;
    }
    public boolean hookedASkyblock() {
        return askyblock;
    }
    public boolean hookedSuperiorSkyblock() {
        return superiorskyblock;
    }
    public boolean hookedEpicSkyblock() {
        return epicskyblock;
    }

    public HashMap<Regional, String> getRegionalIdentifiersAt(Location l) {
        final HashMap<Regional, String> a = new HashMap<>();
        if(hookedFactionsUUID()) {
            a.put(FACTIONS_UUID, FACTIONS_UUID.getRegionalIdentifierAt(l));
        }
        if(hookedASkyblock()) {
            a.put(A_SKY, A_SKY.getRegionalIdentifierAt(l));
        }
        if(hookedSuperiorSkyblock()) {
            a.put(SUPERIOR_SKY, SUPERIOR_SKY.getRegionalIdentifierAt(l));
        }
        if(hookedEpicSkyblock()) {
            a.put(IRIDIUM_SKY, IRIDIUM_SKY.getRegionalIdentifierAt(l));
        }
        return a;
    }
    public boolean allowsPvP(@NotNull Player player, @NotNull Location l) {
        final List<Boolean> booleans = new ArrayList<>();
        if(WORLD_GUARD) {
            booleans.add(WorldGuardAPI.INSTANCE.allowsPvP(player, l));
        }
        if(factionsUUID || askyblock || superiorskyblock || epicskyblock) {
            booleans.add(isPvPZone(l));
        }
        return !booleans.contains(false);
    }
    public boolean isPvPZone(@NotNull Location l) {
        return isPvPZone(l, null);
    }
    public boolean isPvPZone(@NotNull Location l, @Nullable List<String> exceptions) {
        final HashMap<Regional, String> ids = getRegionalIdentifiersAt(l);
        final boolean hasExceptions = exceptions != null;
        for(Regional regional : ids.keySet()) {
            String id = ids.get(regional);
            if(id != null) {
                id = ChatColor.stripColor(id);
                if(hasExceptions && exceptions.contains(id)) {
                    return false;
                }
                switch (id) {
                    case "Safezone":
                        return !(regional instanceof FactionsUUID);
                    case "spawn":
                        return !(regional instanceof ASky);
                    case "":
                        return false;
                    default:
                        return true;
                }
            }
        }
        return false;
    }

    public HashMap<Regional, String> getChatModes(UUID player) {
        final HashMap<Regional, String> modes = new HashMap<>();
        if(hookedFactionsUUID()) {
            modes.put(FACTIONS_UUID, FACTIONS_UUID.getChatMode(player));
        }
        if(hookedASkyblock()) {
            modes.put(A_SKY, A_SKY.getChatMode(player));
        }
        if(hookedSuperiorSkyblock()) {
            modes.put(SUPERIOR_SKY, SUPERIOR_SKY.getChatMode(player));
        }
        if(hookedEpicSkyblock()) {
            modes.put(IRIDIUM_SKY, SUPERIOR_SKY.getChatMode(player));
        }
        return modes;
    }
    public List<Player> getReceivingPlayers(UUID player, HashMap<Regional, String> chatModes) {
        final List<Player> players = new ArrayList<>();
        for(Regional regional : chatModes.keySet()) {
            final List<Player> associates = regional.getOnlineAssociates(player);
            if(associates != null) {
                players.addAll(associates);
            }
        }
        return players;
    }

    private List<UUID> getRelation(UUID player, int type) {
        final List<UUID> uuids = new ArrayList<>();
        if(FACTIONS_UUID != null) {
            add(player, type, FACTIONS_UUID, uuids);
        }
        if(A_SKY != null) {
            add(player, type, A_SKY, uuids);
        }
        if(SUPERIOR_SKY != null) {
            add(player, type, SUPERIOR_SKY, uuids);
        }
        return uuids;
    }
    private void add(UUID player, int type, Regional plugin, List<UUID> list) {
        List<UUID> flist = null;
        switch (type) {
            case 0:
                flist = plugin.getAssociates(player);
                break;
            case 1:
                flist = plugin.getNeutrals(player);
                break;
            case 2:
                flist = plugin.getAllies(player);
                break;
            case 3:
                flist = plugin.getTruces(player);
                break;
            case 4:
                flist = plugin.getEnemies(player);
                break;
            default:
                break;
        }
        if(flist != null) {
            list.addAll(flist);
        }
    }

    public List<UUID> getAssociates(UUID player) {
        return getRelation(player, 0);
    }
    public List<UUID> getNeutrals(UUID player) {
        return getRelation(player, 1);
    }
    public List<UUID> getAllies(UUID player) {
        return getRelation(player, 2);
    }
    public List<UUID> getTruces(UUID player) {
        return getRelation(player, 3);
    }
    public List<UUID> getEnemies(UUID player) {
        return getRelation(player, 4);
    }

    public String getFactionTagAt(Location l) {
        return factionsUUID ? FACTIONS_UUID.getRegionalIdentifierAt(l) : null;
    }
    public String getFactionTag(UUID player) {
        return factionsUUID ? FACTIONS_UUID.getRegionalIdentifier(player) : null;
    }
    public String getFactionTag(OfflinePlayer player) {
        return getFactionTag(player.getUniqueId());
    }
    public List<UUID> getFactionMembers(UUID player) {
        return factionsUUID ? FACTIONS_UUID.getAssociates(player) : null;
    }
}
