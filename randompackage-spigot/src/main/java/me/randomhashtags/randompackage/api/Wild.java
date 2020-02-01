package me.randomhashtags.randompackage.api;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.obj.TObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Wild extends RPFeature implements CommandExecutor {
    private static Wild instance;
    public static Wild getWild() {
        if(instance == null) instance = new Wild();
        return instance;
    }

    private YamlConfiguration config;
    private HashMap<UUID, Long> expirations;
    private long cooldown;
    private HashMap<String, TObject> xcoords, zcoords;
    private List<String> teleportExceptions;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null && cmd.getName().equals("wild")) {
            tryTeleporting(player);
        }
        return true;
    }

    public String getIdentifier() {
        return "WILD";
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "wild.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "wild.yml"));
        cooldown = config.getLong("settings.cooldown");
        expirations = new HashMap<>();
        xcoords = new HashMap<>();
        zcoords = new HashMap<>();
        teleportExceptions = new ArrayList<>();
        teleportExceptions.add("Wilderness");

        for(String s : config.getStringList("settings.x coords")) {
            final String[] values = s.split(";");
            xcoords.put(values[0], new TObject(BigDecimal.valueOf(Double.parseDouble(values[1])), BigDecimal.valueOf(Double.parseDouble(values[2])), null));
        }
        for(String s : config.getStringList("settings.z coords")) {
            final String[] values = s.split(";");
            zcoords.put(values[0], new TObject(BigDecimal.valueOf(Double.parseDouble(values[1])), BigDecimal.valueOf(Double.parseDouble(values[2])), null));
        }
        sendConsoleDidLoadFeature("Wild", started);
    }
    public void unload() {
    }

    private BigDecimal get(World w, HashMap<String, TObject> coords, boolean max) {
        final TObject o = coords.get(w.getName());
        return o != null ? (BigDecimal) (max ? o.getFirst() : o.getSecond()) : null;
    }

    public BigDecimal getMaxX(@NotNull World w) {
        return get(w, xcoords, true);
    }
    public BigDecimal getMinX(@NotNull World w) {
        return get(w, xcoords, false);
    }
    public BigDecimal getMaxZ(@NotNull World w) {
        return get(w, zcoords, true);
    }
    public BigDecimal getMinZ(@NotNull World w) {
        return get(w, zcoords, false);
    }

    public long getCooldownExpireTime(UUID player) {
        return expirations.get(player);
    }
    public boolean isCooldowned(UUID player) {
        if(expirations.containsKey(player) && getCooldownTimeLeft(player) <= 0) {
            expirations.remove(player);
        }
        return expirations.containsKey(player);
    }
    public long getCooldownTimeLeft(UUID player) {
        return expirations.get(player);
    }
    public String getCooldownLeft(UUID player) {
        return !expirations.containsKey(player) ? "" : getRemainingTime(getCooldownExpireTime(player)-System.currentTimeMillis());
    }

    private Location getRandomLocation(World w, BigDecimal minx, BigDecimal maxx, BigDecimal minz, BigDecimal maxz) {
        final int x = getRandomBigDecimal(minx, maxx).intValue(), z = getRandomBigDecimal(minz, maxz).intValue();
        final Location l = new Location(w, x, 256, z);
        l.setY(w.getHighestBlockYAt(l));
        return l;
    }
    public Location getRandomLocation(World w) {
        return getRandomLocation(w, null);
    }
    public Location getRandomLocation(World w, List<String> exceptions) {
        final BigDecimal minx = getMinX(w), maxx = getMaxX(w), minz = getMinZ(w), maxz = getMaxZ(w);
        for(int i = 1; i <= 100; i++) {
            final Location l = getRandomLocation(w, minx, maxx, minz, maxz);
            if(!regions.isPvPZone(l, exceptions)) {
                return l;
            }
        }
        return null;
    }
    public void tryTeleporting(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.wild", true)) {
            final UUID uuid = player.getUniqueId();
            if(isCooldowned(uuid)) {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{TIME}", getCooldownLeft(uuid));
                sendStringListMessage(player, getStringList(config, "messages.on cooldown"), replacements);
            } else {
                if(regions.isPvPZone(player.getLocation())) {
                    sendStringListMessage(player, getStringList(config, "messages.cannot be used in area"), null);
                } else {
                    if(!hasPermission(player, "RandomPackage.wild.bypass", false)) {
                        expirations.put(uuid, System.currentTimeMillis()+cooldown*1000);
                    }
                    final Location l = getRandomLocation(player.getWorld(), teleportExceptions);
                    if(l != null) {
                        player.teleport(l, PlayerTeleportEvent.TeleportCause.COMMAND);
                        sendStringListMessage(player, getStringList(config, "messages.teleported"), null);
                    }
                }
            }
        }
    }
}
