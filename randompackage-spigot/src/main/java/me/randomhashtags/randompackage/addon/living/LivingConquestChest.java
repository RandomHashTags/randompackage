package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.addon.ConquestChest;
import me.randomhashtags.randompackage.addon.obj.ConquestMob;
import me.randomhashtags.randompackage.api.Conquest;
import me.randomhashtags.randompackage.event.ConquestBlockDamageEvent;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class LivingConquestChest implements UVersionableSpigot {
    public static List<LivingConquestChest> LIVING = new ArrayList<>();

    public Location location;
    private final int x, y, z;
    private int announceTask, despawnTask, distanceCheckTask, minutes;
    private HashMap<UUID, LivingConquestMob> mobs;
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
        finish(spawnBosses);
        if(sendMessage) {
            send(type.getSpawnMsg());
        }
    }
    public LivingConquestChest(Location location, ConquestChest type, int health, long spawnedTime, boolean sendMessage, boolean spawnBosses) {
        this.health = health;
        this.location = location;
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.type = type;
        this.spawnedTime = spawnedTime;
        finish(spawnBosses);
        if(sendMessage) {
            send(type.getSpawnMsg());
        }
    }
    private void finish(boolean spawnBosses) {
        final World w = location.getWorld();
        final Block block = w.getBlockAt(location);
        block.setType(type.getPlacedBlock().getMaterial());
        block.getState().update();
        LIVING.add(this);

        final int a = type.getAnnounceIntervalAfterSpawned(), repeat = a*60*20;
        announceTask = SCHEDULER.scheduleSyncRepeatingTask(RANDOM_PACKAGE, () -> {
            minutes += a;
            send(type.getStillAliveMsg());
        }, repeat, repeat);
        despawnTask = SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> delete(false), type.getDespawnDelay()*20*60);

        if(spawnBosses) {
            mobs = new HashMap<>();
            final HashMap<ConquestMob, String> spawnedBosses = type.getSpawnedBosses();
            for(ConquestMob c : spawnedBosses.keySet()) {
                final String target = spawnedBosses.get(c);
                final int min = target.contains("-") ? Integer.parseInt(target.split("-")[0]) : Integer.parseInt(target), max = target.contains("-") ? Integer.parseInt(target.split("-")[1]) : 0, amount = target.contains("-") ? min+RANDOM.nextInt(max-min+1) : min;
                for(int i = 1; i <= amount; i++) {
                    final LivingConquestMob mob = c.spawn(location);
                    mobs.put(mob.getUniqueId(), mob);
                }
            }
            final int interval = type.getBossDistanceCheckInterval();
            distanceCheckTask = SCHEDULER.scheduleSyncRepeatingTask(RANDOM_PACKAGE, () -> checkMobDistance(), interval, interval);
        }
    }

    public void checkMobDistance() {
        if(mobs != null) {
            final double max = type.getBossMaxDistanceFromConquest();
            for(LivingConquestMob mob : mobs.values()) {
                final LivingEntity entity = mob.getEntity();
                if(entity.getLocation().distance(location) > max) {
                    entity.teleport(location.clone().add(0, 1, 0));
                }
            }
        }
    }
    private void send(List<String> msg) {
        final String min = Integer.toString(minutes), X = Integer.toString(x), Y = Integer.toString(y), Z = Integer.toString(z), hp = Integer.toString(health), max = Integer.toString(type.getMaxHP());
        for(String s : msg) {
            s = colorize(s.replace("{MIN}", min).replace("{X}", X).replace("{Y}", Y).replace("{Z}",Z).replace("{HP}", hp).replace("{MAX_HP}", max));
            Bukkit.broadcastMessage(s);
        }
    }
    public void damage(@NotNull Player player, double damage, boolean callEvent) {
        final long time = System.currentTimeMillis();
        final double damageDelay = type.getDamageDelay();
        if(damageDelay <= 0 || time >= damageDelayExpire) {
            if(damageDelay > 0) {
                damageDelayExpire = (long) (time+(damageDelay/20*1000));
            }
            ConquestBlockDamageEvent cde;
            if(callEvent) {
                cde = new ConquestBlockDamageEvent(player, this, type.getDamagePerHit());
                PLUGIN_MANAGER.callEvent(cde);
                if(cde.isCancelled()) return;
            }
            if(LIVING.contains(this)) {
                health -= damage;
                final int healthMsgRadius = type.getHealthMsgRadius();
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", player.getName());
                replacements.put("{X}", Integer.toString(x));
                replacements.put("{Y}", Integer.toString(y));
                replacements.put("{Z}", Integer.toString(z));
                replacements.put("{HP}", Integer.toString(health));
                replacements.put("{MAX_HP}", Integer.toString(type.getMaxHP()));
                final Collection<Entity> nearby = location.getWorld().getNearbyEntities(location, healthMsgRadius, healthMsgRadius, healthMsgRadius);
                final List<String> msg = health <= 0.00 ? type.getUnlockedMsg() : type.getHealthMsg();
                if(health <= 0.00) {
                    conquerer = player.getName();
                    Conquest.INSTANCE.lastConquerer = player.getName();
                    delete(true);
                }
                for(Entity e : nearby) {
                    if(e instanceof Player) {
                        sendStringListMessage(e, msg, replacements);
                    }
                }
            }
        }
    }
    public void delete(boolean dropsRewards) {
        delete(dropsRewards, false);
    }
    public void delete(boolean dropsRewards, boolean despawnMobs) {
        SCHEDULER.cancelTask(announceTask);
        SCHEDULER.cancelTask(despawnTask);
        SCHEDULER.cancelTask(distanceCheckTask);
        final World w = location.getWorld();
        w.getBlockAt(location).setType(Material.AIR);
        if(dropsRewards) {
            for(ItemStack is : getRandomRewards()) {
                w.dropItem(location, is);
            }
        }
        if(despawnMobs) {
            if(mobs != null) {
                for(LivingConquestMob mob : mobs.values()) {
                    mob.getEntity().remove();
                }
            }
        }
        LIVING.remove(this);
    }
    public List<ItemStack> getRandomRewards() {
        final List<ItemStack> r = new ArrayList<>();
        final List<String> rewards = new ArrayList<>(type.getRewards());
        final String rs = type.getRewardSize();
        final String[] split = rs.split("-");
        final int min = rs.contains("-") ? Integer.parseInt(split[0]) : 0, amount = !rs.contains("-") ? Integer.parseInt(rs) : min + RANDOM.nextInt(Integer.parseInt(split[1])-min+1);
        final RandomPackageAPI api = RandomPackageAPI.INSTANCE;
        for(int i = 1; i <= amount; i++) {
            final String reward = rewards.get(RANDOM.nextInt(rewards.size()));
            final ItemStack is = api.createItemStack(null, reward);
            if(is != null && !is.getType().equals(Material.AIR)) {
                r.add(is);
            }
            rewards.remove(reward);
        }
        return r;
    }
    public HashMap<UUID, LivingConquestMob> getMobs() {
        return mobs;
    }

    public static LivingConquestChest valueOf(@NotNull Location l) {
        for(LivingConquestChest c : LIVING) {
            if(c.location.equals(l)) {
                return c;
            }
        }
        return null;
    }
    public static LivingConquestChest valueOf(@NotNull Chunk chunk) {
        for(LivingConquestChest c : LIVING) {
            if(c.location.getChunk().equals(chunk)) {
                return c;
            }
        }
        return null;
    }
    public static LivingConquestChest valueOf(@NotNull UUID conquestMob) {
        for(LivingConquestChest cc : LIVING) {
            final HashMap<UUID, LivingConquestMob> mobs = cc.getMobs();
            if(mobs != null && mobs.containsKey(conquestMob)) {
                return cc;
            }
        }
        return null;
    }

    public static void deleteAll(boolean dropsRewards) {
        if(LIVING != null) {
            ListIterator<LivingConquestChest> iter = LIVING.listIterator();
            while(iter.hasNext()) {
                iter.next().delete(dropsRewards);
                iter.remove();
            }
        }
    }
}
