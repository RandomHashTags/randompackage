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
    private boolean WORLD_GUARD, FACTIONS_UUID, A_SKYBLOCK, SUPERIOR_SKYBLOCK, EPIC_SKYBLOCK;

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
    public boolean isHooked() {
        return FACTIONS_UUID || A_SKYBLOCK || SUPERIOR_SKYBLOCK || EPIC_SKYBLOCK;
    }

    public void trySupportingWorldGuard() {
        WORLD_GUARD = isTrue("supported plugins.regional.WorldGuard") && PLUGIN_MANAGER.isPluginEnabled("WorldGuard");
        if(WORLD_GUARD) {
            hooked("WorldGuard");
        }
    }
    public void trySupportingFactions() {
        FACTIONS_UUID = isTrue("supported plugins.regional.FactionsUUID") && PLUGIN_MANAGER.isPluginEnabled("Factions");
        if(FACTIONS_UUID) {
            FactionsUUID.INSTANCE.enable();
            hooked("FactionsUUID");
            if(RP_CONFIG.getBoolean("faction upgrades.enabled")) {
                FactionUpgrades.getFactionUpgrades().enable();
                new AddFactionPower().load();
            }
        }
    }
    public void trySupportingASkyblock() {
        A_SKYBLOCK = isTrue("supported plugins.regional.ASkyblock") && PLUGIN_MANAGER.isPluginEnabled("ASkyblock");
        if(A_SKYBLOCK) {
            ASky.INSTANCE.enable();
            hooked("ASkyblock");
        }
    }
    public void trySupportingSuperiorSkyblock() {
        SUPERIOR_SKYBLOCK = isTrue("supported plugins.regional.SuperiorSkyblock") && PLUGIN_MANAGER.isPluginEnabled("SuperiorSkyblock");
        if(SUPERIOR_SKYBLOCK) {
            SuperiorSky.INSTANCE.enable();
            hooked("SuperiorSkyblock");
        }
    }
    public void trySupportingEpicSkyblock() {
        EPIC_SKYBLOCK = isTrue("supported plugins.regional.EpicSkyblock") && PLUGIN_MANAGER.isPluginEnabled("EpicSkyblock");
        if(EPIC_SKYBLOCK) {
            IridiumSky.INSTANCE.enable();
            hooked("EpicSkyblock");
        }
    }
    private void hooked(String plugin) {
        sendConsoleMessage("&6[RandomPackage] &aHooked Regional Plugin: " + plugin);
    }
    public boolean hookedFactionsUUID() {
        return FACTIONS_UUID;
    }
    public boolean hookedASkyblock() {
        return A_SKYBLOCK;
    }
    public boolean hookedSuperiorSkyblock() {
        return SUPERIOR_SKYBLOCK;
    }
    public boolean hookedEpicSkyblock() {
        return EPIC_SKYBLOCK;
    }

    public HashMap<Regional, String> getRegionalIdentifiersAt(Location l) {
        final HashMap<Regional, String> identifiers = new HashMap<>();
        if(hookedFactionsUUID()) {
            final FactionsUUID factionsUUID = FactionsUUID.INSTANCE;
            identifiers.put(factionsUUID, factionsUUID.getRegionalIdentifierAt(l));
        }
        if(hookedASkyblock()) {
            final ASky asky = ASky.INSTANCE;
            identifiers.put(asky, asky.getRegionalIdentifierAt(l));
        }
        if(hookedSuperiorSkyblock()) {
            final SuperiorSky superiorSky = SuperiorSky.INSTANCE;
            identifiers.put(superiorSky, superiorSky.getRegionalIdentifierAt(l));
        }
        if(hookedEpicSkyblock()) {
            final IridiumSky iridiumSky = IridiumSky.INSTANCE;
            identifiers.put(iridiumSky, iridiumSky.getRegionalIdentifierAt(l));
        }
        return identifiers;
    }
    public boolean allowsPvP(@NotNull Player player, @NotNull Location l) {
        final List<Boolean> booleans = new ArrayList<>();
        if(WORLD_GUARD) {
            booleans.add(WorldGuardAPI.INSTANCE.allowsPvP(player, l));
        }
        if(FACTIONS_UUID || A_SKYBLOCK || SUPERIOR_SKYBLOCK || EPIC_SKYBLOCK) {
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
            final FactionsUUID factionsUUID = FactionsUUID.INSTANCE;
            modes.put(factionsUUID, factionsUUID.getChatMode(player));
        }
        if(hookedASkyblock()) {
            final ASky asky = ASky.INSTANCE;
            modes.put(asky, asky.getChatMode(player));
        }
        if(hookedSuperiorSkyblock()) {
            final SuperiorSky superiorSky = SuperiorSky.INSTANCE;
            modes.put(superiorSky, superiorSky.getChatMode(player));
        }
        if(hookedEpicSkyblock()) {
            final IridiumSky iridiumSky = IridiumSky.INSTANCE;
            modes.put(iridiumSky, iridiumSky.getChatMode(player));
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
        if(FACTIONS_UUID) {
            add(player, type, FactionsUUID.INSTANCE, uuids);
        }
        if(A_SKYBLOCK) {
            add(player, type, ASky.INSTANCE, uuids);
        }
        if(SUPERIOR_SKYBLOCK) {
            add(player, type, SuperiorSky.INSTANCE, uuids);
        }
        if(EPIC_SKYBLOCK) {
            add(player, type, IridiumSky.INSTANCE, uuids);
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
        return FACTIONS_UUID ? FactionsUUID.INSTANCE.getRegionalIdentifierAt(l) : null;
    }
    public String getFactionTag(UUID player) {
        return FACTIONS_UUID ? FactionsUUID.INSTANCE.getRegionalIdentifier(player) : null;
    }
    public String getFactionTag(OfflinePlayer player) {
        return getFactionTag(player.getUniqueId());
    }
    public List<UUID> getFactionMembers(UUID player) {
        return FACTIONS_UUID ? FactionsUUID.INSTANCE.getAssociates(player) : null;
    }
}
