package me.randomhashtags.randompackage.recode.api.addons.active;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.api.events.ConquestDamageEvent;
import me.randomhashtags.randompackage.utils.classes.ConquestChest;
import me.randomhashtags.randompackage.recode.utils.ConquestMob;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class LivingConquestChest {
    public static List<LivingConquestChest> living;
    private static PluginManager pluginmanager;

    private Random random = new Random();
    private BukkitScheduler scheduler = Bukkit.getScheduler();
    public Location location;
    private int x, y, z;
    private int announceTask, despawnTask, minutes;
    public ConquestChest type;
    public long spawnedTime;
    public long damageDelayExpire;
    public int health;
    public String conquerer;
    public LivingConquestChest(Location location, ConquestChest type, long spawnedTime, boolean sendMessage, boolean spawnBosses) {
        this.health = type.getSpawnedHP();
        this.location = location;
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.type = type;
        this.spawnedTime = spawnedTime;
        q(spawnBosses);
        if(sendMessage) send(type.getSpawnMsg());
    }
    public LivingConquestChest(Location location, ConquestChest type, int health, long spawnedTime, boolean sendMessage, boolean spawnBosses) {
        this.health = health;
        this.location = location;
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.type = type;
        this.spawnedTime = spawnedTime;
        q(spawnBosses);
        if(sendMessage) send(type.getSpawnMsg());
    }
    private void q(boolean spawnBosses) {
        if(living == null) {
            living = new ArrayList<>();
            pluginmanager = Bukkit.getPluginManager();
        }
        final World w = location.getWorld();
        final Block b = w.getBlockAt(location);
        b.setType(type.getPlacedBlock().getMaterial());
        b.getState().update();
        living.add(this);
        final int a = type.getAnnounceIntervalAfterSpawned(), repeat = a*60*20;
        final RandomPackage rp = RandomPackage.getPlugin;
        announceTask = scheduler.scheduleSyncRepeatingTask(rp, () -> {
            minutes += a;
            send(type.getStillAliveMsg());
        }, repeat, repeat);
        despawnTask = scheduler.scheduleSyncDelayedTask(rp, () -> delete(false), type.getDespawnDelay()*20*60);
        if(spawnBosses) {
            final HashMap<ConquestMob, String> spawnedBosses = type.getSpawnedBosses();
            for(ConquestMob c : spawnedBosses.keySet()) {
                final String target = spawnedBosses.get(c);
                final int min = target.contains("-") ? Integer.parseInt(target.split("-")[0]) : Integer.parseInt(target), max = target.contains("-") ? Integer.parseInt(target.split("-")[1]) : 0, amount = target.contains("-") ? min+random.nextInt(max-min+1) : min;
                for(int i = 1; i <= amount; i++)
                    c.spawn(location);
            }
        }
    }
    private void send(List<String> msg) {
        for(String s : msg) {
            s = ChatColor.translateAlternateColorCodes('&', s.replace("{MIN}", Integer.toString(minutes)).replace("{X}", Integer.toString(x)).replace("{Y}", Integer.toString(y)).replace("{Z}", Integer.toString(z)).replace("{HP}", Integer.toString(health)).replace("{MAX_HP}", Integer.toString(type.getMaxHP())));
            Bukkit.broadcastMessage(s);
        }
    }
    public void damage(Player player, double damage, boolean callEvent) {
        final long t = System.currentTimeMillis();
        final double d = type.getDamageDelay();
        if(d <= 0 || t >= damageDelayExpire) {
            if(d > 0) damageDelayExpire = (long) (t+(d/20*1000));
            ConquestDamageEvent cde;
            if(callEvent) {
                cde = new ConquestDamageEvent(player, this, type.getDamagePerHit());
                pluginmanager.callEvent(cde);
                if(cde.isCancelled()) return;
            }
            if(living.contains(this)) {
                health -= damage;
                final int r = type.getHealthMsgRadius();
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", player.getName());
                replacements.put("{X}", Integer.toString(x));
                replacements.put("{Y}", Integer.toString(y));
                replacements.put("{Z}", Integer.toString(z));
                replacements.put("{HP}", Integer.toString(health));
                replacements.put("{MAX_HP}", Integer.toString(type.getMaxHP()));
                final Collection<Entity> nearby = location.getWorld().getNearbyEntities(location, r, r, r);
                final List<String> msg = health <= 0.00 ? type.getUnlockedMsg() : type.getHealthMsg();
                if(health <= 0.00) {
                    conquerer = player.getName();
                    delete(true);
                }
                for(Entity e : nearby)
                    if(e instanceof Player)
                        api.sendStringListMessage(e, msg, replacements);
            }
        }
    }
    public void delete(boolean dropsRewards) {
        scheduler.cancelTask(announceTask);
        scheduler.cancelTask(despawnTask);
        final World w = location.getWorld();
        w.getBlockAt(location).setType(Material.AIR);
        if(dropsRewards) {
            for(ItemStack is : getRandomRewards()) {
                w.dropItem(location, is);
            }
        }
        living.remove(this);
        random = null;
        scheduler = null;
        location = null;
        x = 0;
        y = 0;
        z = 0;
        announceTask = 0;
        despawnTask = 0;
        minutes = 0;
        type = null;
        spawnedTime = 0;
        damageDelayExpire = 0;
        health = 0;
        conquerer = null;
        if(living.isEmpty()) {
            living = null;
            pluginmanager = null;
        }
    }
    public List<ItemStack> getRandomRewards() {
        final Random random = new Random();
        final List<ItemStack> r = new ArrayList<>();
        final List<String> rewards = new ArrayList<>(type.getRewards());
        final String rs = type.getRewardSize();
        final int min = rs.contains("-") ? Integer.parseInt(rs.split("-")[0]) : 0, amount = !rs.contains("-") ? Integer.parseInt(rs) : min + random.nextInt(Integer.parseInt(rs.split("-")[1])-min+1);
        for(int i = 1; i <= amount; i++) {
            final String reward = rewards.get(random.nextInt(rewards.size()));
            final ItemStack is = api.d(null, reward);
            if(is != null && !is.getType().equals(Material.AIR)) r.add(is);
            rewards.remove(reward);
        }
        return r;
    }
    public static LivingConquestChest valueOf(Location l) {
        if(living != null) {
            for(LivingConquestChest c : living)
                if(c.location.equals(l))
                    return c;
        }
        return null;
    }
    public static LivingConquestChest valueOf(Chunk chunk) {
        if(living != null) {
            for(LivingConquestChest c : living)
                if(c.location.getChunk().equals(chunk))
                    return c;
        }
        return null;
    }

    public static void deleteAll(boolean dropsRewards) {
        if(living != null) {
            ListIterator<LivingConquestChest> iter = living.listIterator();
            while(iter.hasNext()){
                iter.next().delete(dropsRewards);
                iter.remove();
            }
            living = null;
            pluginmanager = null;
        }
    }
}
