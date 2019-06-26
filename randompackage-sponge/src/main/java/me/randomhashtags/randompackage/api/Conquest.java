package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.api.events.conquest.ConquestDamageEvent;
import me.randomhashtags.randompackage.utils.classes.conquests.ConquestChest;
import me.randomhashtags.randompackage.utils.classes.conquests.ConquestMob;
import me.randomhashtags.randompackage.utils.classes.conquests.LivingConquestChest;
import me.randomhashtags.randompackage.utils.classes.conquests.LivingConquestMob;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Conquest extends RandomPackageAPI {

    public boolean isEnabled = false;
    private static Conquest instance;
    public static Conquest getConquest() {
        if(instance == null) instance = new Conquest();
        return instance;
    }
    public FileConfiguration config;
    private List<Integer> tasks;
    private LivingConquestChest last;

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

    public void enable() {
        final long started = System.currentTimeMillis();
        if(isEnabled) return;
        save(null, "conquests.yml");
        eventmanager.registerListeners(randompackage, this);
        isEnabled = true;
        config = YamlConfiguration.loadConfiguration(new File(rpd, "conquests.yml"));

        final YamlConfiguration a = otherdata;
        if(!a.getBoolean("saved default conquests")) {
            save("conquests", "NORMAL.yml");
            a.set("saved default conquests", true);
            saveOtherData();
        }

        tasks = new ArrayList<>();

        for(String s : config.getConfigurationSection("bosses").getKeys(false)) {
            final String p = "bosses." + s + ".";
            new ConquestMob(s, config.getString(p + "type").toUpperCase(), translateColorCodes(config.getString(p + "name")), config.getStringList(p + "attributes"), config.getStringList(p + "equipment"), config.getStringList(p + "drops"));
        }
        for(File f : new File(rpd + separator + "conquests").listFiles()) {
            final ConquestChest c = new ConquestChest(YamlConfiguration.loadConfiguration(f), f.getName());
            final int spawninterval = c.getSpawnInterval()*20;
            tasks.add(scheduler.scheduleSyncRepeatingTask(randompackage, () -> {
                final String[] sr = c.getSpawnRegion().split(";");
                final World w = Bukkit.getWorld(sr[0]);
                final int xMin = Integer.parseInt(sr[1].split(":")[0]), xMax = Integer.parseInt(sr[1].split(":")[1]), x = xMin + random.nextInt(xMax-xMin+1), zMin = Integer.parseInt(sr[2].split(":")[0]), zMax = Integer.parseInt(sr[2].split(":")[1]), z = zMin + random.nextInt(zMax-zMin+1);
                final Location l = new Location(w, x, 256, z);
                l.setY(w.getHighestBlockYAt(l));
                last = c.spawn(l);
            }, spawninterval, spawninterval));
        }

        final List<String> conquests = a.getStringList("conquests");
        final HashMap<String, ConquestChest> cc = ConquestChest.types;
        if(conquests != null && !conquests.isEmpty()) {
            for(String s : conquests) {
                final String[] p = s.split(":");
                new LivingConquestChest(toLocation(p[0]), cc.get(p[3]), Integer.parseInt(p[2]), Long.parseLong(p[1]), false, false);
            }
        }
        final HashMap<String, ConquestMob> CM = ConquestMob.bosses;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (cc != null ? cc.size() : 0) + " conquest chests and " + (CM != null ? CM.size() : 0) + " bosses &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        config = null;
        for(int i : tasks) scheduler.cancelTask(i);
        tasks = null;

        LivingConquestMob.deleteAll();
        final List<LivingConquestChest> C = LivingConquestChest.living;
        if(C != null) {
            final List<String> con = new ArrayList<>();
            for(LivingConquestChest c : C) {
                con.add(toString(c.location) + ":" + c.spawnedTime + ":" + c.health + ":" + c.type.ymlName + ":" + c.conquerer);
            }
            otherdata.set("conquests", con);
        }
        destroyConquests();
        saveOtherData();

        LivingConquestChest.deleteAll(false);
        ConquestChest.deleteAll();
        ConquestMob.deleteAll();
        isEnabled = false;
        eventmanager.unregisterListeners(this);
    }
    public void destroyConquests() {
        final List<LivingConquestChest> C = LivingConquestChest.living;
        if(C != null) {
            for(LivingConquestChest l : new ArrayList<>(C)) {
                l.delete(false);
            }
        }
    }

    @Listener
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Block c = event.getClickedBlock();
        if(c != null) {
            final LivingConquestChest cc = LivingConquestChest.valueOf(c.getLocation());
            if(cc != null) {
                final Player player = event.getPlayer();
                event.setCancelled(true);
                player.updateInventory();
                if(!event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
                final ConquestDamageEvent cde = new ConquestDamageEvent(player, cc, cc.type.getDamagePerHit());
                pluginmanager.callEvent(cde);
                if(!cde.isCancelled()) {
                    cc.damage(player, cde.damage, false);
                }
            }
        }
    }
    @Listener
    private void blockBreakEvent(BlockBreakEvent event) {
        final LivingConquestChest c = LivingConquestChest.valueOf(event.getBlock().getLocation());
        if(c != null) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @Listener
    private void entityDeathEvent(EntityDeathEvent event) {
        final HashMap<UUID, LivingConquestMob> L = LivingConquestMob.living;
        if(L != null) {
            final LivingConquestMob l = L.getOrDefault(event.getEntity().getUniqueId(), null);
            if(l != null) {
                l.kill(event);
            }
        }
    }

    @Listener
    private void chunkUnloadEvent(ChunkUnloadEvent event) {
        final Chunk c = event.getChunk();
        final LivingConquestChest l = LivingConquestChest.valueOf(c);
        if(l != null && event instanceof Cancellable && !event.isCancelled()) {
            event.setCancelled(true);
        }
    }

    public void viewLast(CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.conquest", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            final boolean j = last != null;
            final Location L = j ? last.location : null;
            replacements.put("{LAST}", j ? (System.currentTimeMillis()-last.spawnedTime) + "ms" : "N/A");
            replacements.put("{LOCATION}", j ? L.getBlockX() + "x " + L.getBlockY() + "y " + L.getBlockZ() + "z" : "N/A");
            replacements.put("{CONQUERER}", j && last.conquerer != null ? last.conquerer : "N/A");
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
            final List<ConquestChest> chests = new ArrayList<>(ConquestChest.types.values());
            final Location L = player.getLocation(), l = new Location(L.getWorld(), L.getBlockX(), L.getBlockY(), L.getBlockZ());
            last = chests.get(random.nextInt(chests.size())).spawn(l);
        }
    }
}
