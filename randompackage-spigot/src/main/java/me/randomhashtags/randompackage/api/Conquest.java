package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import me.randomhashtags.randompackage.addon.ConquestChest;
import me.randomhashtags.randompackage.addon.file.FileConquestChest;
import me.randomhashtags.randompackage.addon.living.LivingConquestChest;
import me.randomhashtags.randompackage.addon.living.LivingConquestMob;
import me.randomhashtags.randompackage.addon.obj.ConquestMob;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.ConquestBlockDamageEvent;
import me.randomhashtags.randompackage.perms.ConquestPermission;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Conquest extends RPFeatureSpigot implements CommandExecutor {
    public static final Conquest INSTANCE = new Conquest();

    public YamlConfiguration config;
    private List<Integer> tasks;
    private long lastSpawnTime;
    private Location lastLocation;
    public String lastConquerer;

    @Override
    public String getIdentifier() {
        return "CONQUEST";
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(args.length == 0) {
            viewLast(sender);
        } else {
            switch (args[0]) {
                case "stop":
                    destroyConquests(sender);
                    break;
                case "spawn":
                    if(sender instanceof Player) {
                        spawn((Player) sender);
                    }
                    break;
                default:
                    viewHelp(sender);
                    break;
            }
        }
        return true;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = DATA_FOLDER + SEPARATOR + "conquests";
        save("conquests", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        if(!OTHER_YML.getBoolean("saved default conquests")) {
            generateDefaultConquests();
            OTHER_YML.set("saved default conquests", true);
            saveOtherData();
        }

        tasks = new ArrayList<>();

        for(String s : getConfigurationSectionKeys(config, "bosses", false)) {
            final String p = "bosses." + s + ".";
            new ConquestMob(s, config.getString(p + "type").toUpperCase(), colorize(config.getString(p + "name")), config.getStringList(p + "attributes"), config.getStringList(p + "equipment"), config.getStringList(p + "drops"));
        }
        for(File f : getFilesInFolder(folder)) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FileConquestChest c = new FileConquestChest(f);
                final int spawninterval = c.getSpawnInterval()*20;
                tasks.add(SCHEDULER.scheduleSyncRepeatingTask(RANDOM_PACKAGE, () -> {
                    spawn(c, getRandomLocation(c));
                }, spawninterval, spawninterval));
            }
        }

        final List<String> conquests = OTHER_YML.getStringList("conquests");
        if(!conquests.isEmpty()) {
            for(String s : conquests) {
                final String[] p = s.split(":");
                new LivingConquestChest(toLocation(p[0]), getConquestChest(p[3]), Integer.parseInt(p[2]), Long.parseLong(p[1]), false, false);
            }
        }
        final HashMap<String, ConquestMob> bosses = ConquestMob.BOSSES;
        sendConsoleDidLoadFeature(getAll(Feature.CONQUEST_CHEST).size() + " conquest chests and " + (bosses != null ? bosses.size() : 0) + " bosses", started);
    }
    @Override
    public void unload() {
        for(int i : tasks) {
            SCHEDULER.cancelTask(i);
        }

        LivingConquestMob.deleteAll();
        final List<LivingConquestChest> livingChests = LivingConquestChest.LIVING;
        if(livingChests != null) {
            final List<String> chests = new ArrayList<>();
            for(LivingConquestChest c : livingChests) {
                chests.add(toString(c.location) + ":" + c.spawnedTime + ":" + c.health + ":" + c.type.getIdentifier() + ":" + c.conquerer);
            }
            OTHER_YML.set("conquests", chests);
        }
        destroyConquests(null);
        saveOtherData();

        LivingConquestChest.deleteAll(false);
        ConquestMob.deleteAll();
        unregister(Feature.CONQUEST_CHEST);
    }

    public void viewLast(@NotNull CommandSender sender) {
        if(hasPermission(sender, ConquestPermission.VIEW_LAST, true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{LAST}", lastSpawnTime > 0 ? (System.currentTimeMillis()-lastSpawnTime) + "ms" : "N/A");
            replacements.put("{LOCATION}", lastLocation != null ? lastLocation.getBlockX() + "x " + lastLocation.getBlockY() + "y " + lastLocation.getBlockZ() + "z" : "N/A");
            replacements.put("{CONQUERER}", lastConquerer != null ? lastConquerer : "N/A");
            sendStringListMessage(sender, getStringList(config, "messages.command"), replacements);
        }
    }
    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, ConquestPermission.VIEW_HELP, true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }
    public void spawn(@NotNull Player player) {
        if(hasPermission(player, ConquestPermission.SPAWN, true)) {
            final List<ConquestChest> chests = new ArrayList<>(getAllConquestChests().values());
            final Location L = player.getLocation(), l = new Location(L.getWorld(), L.getBlockX(), L.getBlockY(), L.getBlockZ());
            spawn(chests.get(RANDOM.nextInt(chests.size())), l);
        }
    }
    public void destroyConquests(@Nullable CommandSender sender) {
        if(sender == null || hasPermission(sender, ConquestPermission.STOP, true)) {
            final List<LivingConquestChest> living = LivingConquestChest.LIVING;
            if(living != null) {
                for(LivingConquestChest chest : new ArrayList<>(living)) {
                    chest.delete(false, true);
                }
            }
        }
    }
    public void spawn(@NotNull ConquestChest chest, @NotNull Location l) {
        chest.spawn(l);
        lastSpawnTime = System.currentTimeMillis();
        lastLocation = l;
        lastConquerer = null;
    }
    public Location getRandomLocation(@NotNull ConquestChest chest) {
        final String[] spawnRegion = chest.getSpawnRegion().split(";");
        final String[] xValues = spawnRegion[1].split(":"), zValues = spawnRegion[2].split(":");
        final String targetWorld = spawnRegion[0];
        final World world = Bukkit.getWorld(targetWorld);
        if(world != null) {
            final int xMin = Integer.parseInt(xValues[0]), xMax = Integer.parseInt(xValues[1]), zMin = Integer.parseInt(zValues[0]), zMax = Integer.parseInt(zValues[1]);
            final int xDifference = xMax-xMin, zDifference = zMax-zMin;
            final int xNum = xDifference < 0 ? -1 : 1, zNum = zDifference < 0 ? -1 : 1;
            final int X = xNum*xDifference, Z = zNum*zDifference;
            final int x = xNum*(xMax- RANDOM.nextInt(X)), z = zNum*(zMax - RANDOM.nextInt(Z));
            final Location location = new Location(world, x, world.getMaxHeight(), z);
            location.setY(world.getHighestBlockYAt(location));
            return location;
        } else {
            sendConsoleMessage("&6[RandomPackage] &cERROR &eInvalid world &f\"" + targetWorld + "\"&e for conquest \"" + chest.getIdentifier() + "\" spawn location!");
            return null;
        }
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if(block != null) {
            final LivingConquestChest chest = LivingConquestChest.valueOf(block.getLocation());
            if(chest != null) {
                final Player player = event.getPlayer();
                event.setCancelled(true);
                player.updateInventory();
                if(!event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    return;
                }
                final ConquestBlockDamageEvent cde = new ConquestBlockDamageEvent(player, chest, chest.type.getDamagePerHit());
                PLUGIN_MANAGER.callEvent(cde);
                if(!cde.isCancelled()) {
                    chest.damage(player, cde.getDamage(), false);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockBreakEvent(BlockBreakEvent event) {
        final LivingConquestChest chest = LivingConquestChest.valueOf(event.getBlock().getLocation());
        if(chest != null) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }
    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final HashMap<UUID, LivingConquestMob> living = LivingConquestMob.LIVING;
        if(living != null) {
            final UUID uuid = event.getEntity().getUniqueId();
            final LivingConquestMob mob = living.getOrDefault(uuid, null);
            if(mob != null) {
                final LivingConquestChest chest = LivingConquestChest.valueOf(uuid);
                if(chest != null) {
                    chest.getMobs().remove(uuid);
                }
                mob.kill(event);
            }
        }
    }
    @EventHandler
    private void chunkUnloadEvent(ChunkUnloadEvent event) {
        final LivingConquestChest chest = LivingConquestChest.valueOf(event.getChunk());
        if(chest != null && event instanceof Cancellable) {
            final Cancellable cancellable = (Cancellable) event;
            cancellable.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void entityTargetLivingEntityEvent(EntityTargetLivingEntityEvent event) {
        final Entity entity = event.getEntity();
        final LivingEntity target = event.getTarget();
        final HashMap<UUID, LivingConquestMob> living = LivingConquestMob.LIVING;
        if(living != null) {
            final LivingConquestMob mob = target != null ? living.getOrDefault(entity.getUniqueId(), null) : null;
            if(mob != null && living.containsKey(target.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}
