package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.ConquestChest;
import me.randomhashtags.randompackage.addon.living.LivingConquestChest;
import me.randomhashtags.randompackage.addon.living.LivingConquestMob;
import me.randomhashtags.randompackage.addon.obj.ConquestMob;
import me.randomhashtags.randompackage.event.ConquestBlockDamageEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.addon.FileConquestChest;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Conquest extends RPFeature implements CommandExecutor {
    private static Conquest instance;
    public static Conquest getConquest() {
        if(instance == null) instance = new Conquest();
        return instance;
    }
    public FileConfiguration config;
    private List<Integer> tasks;
    private long lastSpawnTime;
    private Location lastLocation;
    public String lastConquerer;

    public String getIdentifier() { return "CONQUEST"; }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(args.length == 0) {
            viewLast(sender);
        } else {
            final String a = args[0];
            if(a.equals("help")) {
                viewHelp(sender);
            } else if(a.equals("stop")) {
                destroyConquests();
            } else if(sender instanceof Player && a.equals("spawn")) {
                spawn((Player) sender);
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = dataFolder + separator + "conquests";
        save("conquests", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        if(!otherdata.getBoolean("saved default conquests")) {
            save("conquests", "NORMAL.yml");
            otherdata.set("saved default conquests", true);
            saveOtherData();
        }

        tasks = new ArrayList<>();

        for(String s : config.getConfigurationSection("bosses").getKeys(false)) {
            final String p = "bosses." + s + ".";
            new ConquestMob(s, config.getString(p + "type").toUpperCase(), colorize(config.getString(p + "name")), config.getStringList(p + "attributes"), config.getStringList(p + "equipment"), config.getStringList(p + "drops"));
        }
        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FileConquestChest c = new FileConquestChest(f);
                final int spawninterval = c.getSpawnInterval()*20;
                tasks.add(scheduler.scheduleSyncRepeatingTask(randompackage, () -> {
                    spawn(c, getRandomLocation(c));
                }, spawninterval, spawninterval));
            }
        }

        final List<String> conquests = otherdata.getStringList("conquests");
        if(!conquests.isEmpty()) {
            for(String s : conquests) {
                final String[] p = s.split(":");
                new LivingConquestChest(toLocation(p[0]), getConquestChest(p[3]), Integer.parseInt(p[2]), Long.parseLong(p[1]), false, false);
            }
        }
        final HashMap<String, ConquestMob> CM = ConquestMob.bosses;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (conquestchests != null ? conquestchests.size() : 0) + " conquest chests and " + (CM != null ? CM.size() : 0) + " bosses &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(int i : tasks) {
            scheduler.cancelTask(i);
        }

        LivingConquestMob.deleteAll();
        final List<LivingConquestChest> C = LivingConquestChest.living;
        if(C != null) {
            final List<String> con = new ArrayList<>();
            for(LivingConquestChest c : C) {
                con.add(toString(c.location) + ":" + c.spawnedTime + ":" + c.health + ":" + c.type.getIdentifier() + ":" + c.conquerer);
            }
            otherdata.set("conquests", con);
        }
        destroyConquests();
        saveOtherData();

        LivingConquestChest.deleteAll(false);
        conquestchests = null;
        ConquestMob.deleteAll();
    }
    public void destroyConquests() {
        final List<LivingConquestChest> C = LivingConquestChest.living;
        if(C != null) {
            for(LivingConquestChest l : new ArrayList<>(C)) {
                l.delete(false);
                C.remove(l);
            }
        }
    }
    public void spawn(ConquestChest chest, Location l) {
        chest.spawn(l);
        lastSpawnTime = System.currentTimeMillis();
        lastLocation = l;
        lastConquerer = null;
    }
    public Location getRandomLocation(ConquestChest chest) {
        final String[] sr = chest.getSpawnRegion().split(";");
        final String[] xValues = sr[1].split(":"), zValues = sr[2].split(":");
        final String world = sr[0];
        final World w = Bukkit.getWorld(world);
        if(w != null) {
            final int xMin = Integer.parseInt(xValues[0]), xMax = Integer.parseInt(xValues[1]), zMin = Integer.parseInt(zValues[0]), zMax = Integer.parseInt(zValues[1]);
            final int xDifference = xMax-xMin, zDifference = zMax-zMin;
            final int xNum = xDifference < 0 ? -1 : 1, zNum = zDifference < 0 ? -1 : 1;
            final int X = xNum*xDifference, Z = zNum*zDifference;
            final int x = xNum*(xMax-random.nextInt(X)), z = zNum*(zMax-random.nextInt(Z));
            final Location l = new Location(w, x, 256, z);
            l.setY(w.getHighestBlockYAt(l));
            return l;
        } else {
            sendConsoleMessage("&6[RandomPackage] &cERROR &eInvalid world &f\"" + world + "\"&e for conquest \"" + chest.getIdentifier() + "\" spawn location!");
            return null;
        }
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Block c = event.getClickedBlock();
        if(c != null) {
            final LivingConquestChest cc = LivingConquestChest.valueOf(c.getLocation());
            if(cc != null) {
                final Player player = event.getPlayer();
                event.setCancelled(true);
                player.updateInventory();
                if(!event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
                final ConquestBlockDamageEvent cde = new ConquestBlockDamageEvent(player, cc, cc.type.getDamagePerHit());
                pluginmanager.callEvent(cde);
                if(!cde.isCancelled()) {
                    cc.damage(player, cde.getDamage(), false);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockBreakEvent(BlockBreakEvent event) {
        final LivingConquestChest c = LivingConquestChest.valueOf(event.getBlock().getLocation());
        if(c != null) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final HashMap<UUID, LivingConquestMob> L = LivingConquestMob.living;
        if(L != null) {
            final LivingConquestMob l = L.getOrDefault(event.getEntity().getUniqueId(), null);
            if(l != null) {
                l.kill(event);
            }
        }
    }

    @EventHandler
    private void chunkUnloadEvent(ChunkUnloadEvent event) {
        final Chunk c = event.getChunk();
        final LivingConquestChest l = LivingConquestChest.valueOf(c);
        if(l != null && event instanceof Cancellable) {
            final Cancellable ca = (Cancellable) event;
            if(!ca.isCancelled()) {
                ca.setCancelled(true);
            }
        }
    }

    public void viewLast(CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.conquest", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{LAST}", lastSpawnTime > 0 ? (System.currentTimeMillis()-lastSpawnTime) + "ms" : "N/A");
            replacements.put("{LOCATION}", lastLocation != null ? lastLocation.getBlockX() + "x " + lastLocation.getBlockY() + "y " + lastLocation.getBlockZ() + "z" : "N/A");
            replacements.put("{CONQUERER}", lastConquerer != null ? lastConquerer : "N/A");
            sendStringListMessage(sender, config.getStringList("messages.command"), replacements);
        }
    }
    public void viewHelp(CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.conquest.help", true)) {
            sendStringListMessage(sender, config.getStringList("messages.help"), null);
        }
    }
    public void spawn(Player player) {
        if(hasPermission(player, "RandomPackage.conquest.spawn", true)) {
            final List<ConquestChest> chests = new ArrayList<>(conquestchests.values());
            final Location L = player.getLocation(), l = new Location(L.getWorld(), L.getBlockX(), L.getBlockY(), L.getBlockZ());
            spawn(chests.get(random.nextInt(chests.size())), l);
        }
    }
}
