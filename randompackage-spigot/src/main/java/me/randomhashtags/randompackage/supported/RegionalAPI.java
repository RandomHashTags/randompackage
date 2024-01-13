package me.randomhashtags.randompackage.supported;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.api.FactionUpgrades;
import me.randomhashtags.randompackage.attribute.faction.AddFactionPower;
import me.randomhashtags.randompackage.supported.regional.*;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public enum RegionalAPI implements UVersionableSpigot {
    INSTANCE;

    private boolean WORLD_GUARD, FACTIONS_UUID, A_SKYBLOCK, SUPERIOR_SKYBLOCK, EPIC_SKYBLOCK;

    private boolean isTrue(@NotNull String path) {
        return RandomPackage.INSTANCE.config.getBoolean(path);
    }
    public void setup() {
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
                FactionUpgrades.INSTANCE.enable();
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
        sendConsoleMessage("&aHooked Regional Plugin: " + plugin);
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

    @NotNull
    public HashMap<Regional, String> getRegionalIdentifiersAt(@NotNull Location l) {
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

    @NotNull
    public HashMap<Regional, String> getChatModes(@NotNull UUID player) {
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
    @NotNull
    public List<Player> getReceivingPlayers(@NotNull UUID player, @NotNull HashMap<Regional, String> chatModes) {
        final List<Player> players = new ArrayList<>();
        for(Regional regional : chatModes.keySet()) {
            final Collection<Player> associates = regional.getOnlineAssociates(player);
            players.addAll(associates);
        }
        return players;
    }

    @NotNull
    private List<UUID> getRelation(@NotNull UUID player, int type) {
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
        Collection<UUID> flist = null;
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

    @NotNull
    public List<UUID> getAssociates(@NotNull UUID player) {
        return getRelation(player, 0);
    }
    @NotNull
    public List<UUID> getNeutrals(@NotNull UUID player) {
        return getRelation(player, 1);
    }
    @NotNull
    public List<UUID> getAllies(@NotNull UUID player) {
        return getRelation(player, 2);
    }
    @NotNull
    public List<UUID> getTruces(@NotNull UUID player) {
        return getRelation(player, 3);
    }
    @NotNull
    public List<UUID> getEnemies(@NotNull UUID player) {
        return getRelation(player, 4);
    }

    @Nullable
    public String getFactionTagAt(@NotNull Location l) {
        return FACTIONS_UUID ? FactionsUUID.INSTANCE.getRegionalIdentifierAt(l) : null;
    }
    @Nullable
    public String getFactionTag(@NotNull UUID player) {
        return FACTIONS_UUID ? FactionsUUID.INSTANCE.getRegionalIdentifier(player) : null;
    }
    @Nullable
    public String getFactionTag(@NotNull OfflinePlayer player) {
        return getFactionTag(player.getUniqueId());
    }
    public List<UUID> getFactionMembers(@NotNull UUID player) {
        return FACTIONS_UUID ? FactionsUUID.INSTANCE.getAssociates(player) : null;
    }
}
