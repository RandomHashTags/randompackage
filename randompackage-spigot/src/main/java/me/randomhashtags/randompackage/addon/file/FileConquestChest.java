package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.ConquestChest;
import me.randomhashtags.randompackage.addon.obj.ConquestMob;
import me.randomhashtags.randompackage.addon.living.LivingConquestChest;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public final class FileConquestChest extends RPAddonSpigot implements ConquestChest {
    private HashMap<ConquestMob, String> spawnedBosses;
    private UMaterial placedBlock;

    public FileConquestChest(File f) {
        load(f);
        register(Feature.CONQUEST_CHEST, this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String getSpawnRegion() { return yml.getString("settings.spawn region").toUpperCase(); }
    public String getRewardSize() { return yml.getString("settings.reward size"); }
    public HashMap<ConquestMob, String> getSpawnedBosses() {
        if(spawnedBosses == null) {
            spawnedBosses = new HashMap<>();
            for(String s : yml.getStringList("spawned bosses")) {
                final String[] a = s.split(":");
                spawnedBosses.put(ConquestMob.BOSSES.get(a[0]), a[1]);
            }
        }
        return spawnedBosses;
    }
    public int getSpawnInterval() { return yml.getInt("settings.spawn interval"); }
    public int getHealthMsgRadius() { return yml.getInt("messages.health radius"); }
    public int getMaxHP() { return yml.getInt("settings.max hp"); }
    public int getSpawnedHP() { return yml.getInt("settings.spawned hp"); }
    public int getDamagePerHit() { return yml.getInt("settings.dmg per hit"); }
    public int getFirstAnnounced() { return yml.getInt("messages.first announced"); }
    public int getAnnounceIntervalAfterSpawned() { return yml.getInt("messages.announce interval after spawned"); }
    public int getDespawnDelay() { return yml.getInt("messages.despawn delay"); }
    public double getDamageDelay() { return yml.getDouble("settings.dmg delay"); }
    public UMaterial getPlacedBlock() {
        if(placedBlock == null) placedBlock = UMaterial.match(yml.getString("settings.placed block").toUpperCase());
        return placedBlock;
    }
    public List<String> getRewards() { return getStringList(yml, "rewards"); }
    public List<String> getHitAttributes() { return getStringList(yml, "hit attributes"); }
    public List<String> getSpawnMsg() { return getStringList(yml, "messages.spawn"); }
    public List<String> getWillSpawnMsg() { return getStringList(yml, "messages.will spawn"); }
    public List<String> getStillAliveMsg() { return getStringList(yml, "messages.still alive"); }
    public List<String> getHealthMsg() { return getStringList(yml, "messages.health"); }
    public List<String> getUnlockedMsg() { return getStringList(yml, "messages.unlocked"); }

    public LivingConquestChest spawn(Location l) {
        final Chunk c = l.getChunk();
        if(!c.isLoaded()) c.load();
        return new LivingConquestChest(l, this, System.currentTimeMillis(), true, true);
    }

    public double getBossMaxDistanceFromConquest() {
        return yml.getDouble("settings.bosses.max distance from conquest", 50);
    }
    public int getBossDistanceCheckInterval() {
        return yml.getInt("settings.bosses.distance check interval", 60);
    }
}