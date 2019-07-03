package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.ConquestChest;
import me.randomhashtags.randompackage.addons.objects.ConquestMob;
import me.randomhashtags.randompackage.addons.active.LivingConquestChest;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class FileConquestChest extends ConquestChest {
    private String spawnRegion, rewardSize;
    private HashMap<ConquestMob, String> spawnedBosses;
    private UMaterial placedBlock;
    private List<String> spawnMsg, willSpawnMsg, stillAliveMsg, healthMsg, unlockedMsg;

    public FileConquestChest(File f) {
        load(f);
        initilize();
    }
    public void initilize() { }

    public String getSpawnRegion() {
        if(spawnRegion == null) spawnRegion = yml.getString("settings.spawn region").toUpperCase();
        return spawnRegion;
    }
    public String getRewardSize() {
        if(rewardSize == null) rewardSize = yml.getString("settings.reward size");
        return rewardSize;
    }
    public HashMap<ConquestMob, String> getSpawnedBosses() {
        if(spawnedBosses == null) {
            spawnedBosses = new HashMap<>();
            for(String s : yml.getStringList("bosses")) {
                final String[] a = s.split(":");
                spawnedBosses.put(ConquestMob.bosses.get(a[0]), a[1]);
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
    public List<String> getRewards() { return yml.getStringList("rewards"); }
    public List<String> getHitAttributes() { return yml.getStringList("hit attributes"); }
    public List<String> getSpawnMsg() {
        if(spawnMsg == null) spawnMsg = api.colorizeListString(yml.getStringList("messages.spawn"));
        return spawnMsg;
    }
    public List<String> getWillSpawnMsg() {
        if(willSpawnMsg == null) willSpawnMsg = api.colorizeListString(yml.getStringList("messages.will spawn"));
        return willSpawnMsg;
    }
    public List<String> getStillAliveMsg() {
        if(stillAliveMsg == null) stillAliveMsg = api.colorizeListString(yml.getStringList("messages.still alive"));
        return stillAliveMsg;
    }
    public List<String> getHealthMsg() {
        if(healthMsg == null) healthMsg = api.colorizeListString(yml.getStringList("messages.health"));
        return healthMsg;
    }
    public List<String> getUnlockedMsg() {
        if(unlockedMsg == null) unlockedMsg = api.colorizeListString(yml.getStringList("messages.unlocked"));
        return unlockedMsg;
    }

    public LivingConquestChest spawn(Location l) {
        final Chunk c = l.getChunk();
        if(!c.isLoaded()) c.load();
        return new LivingConquestChest(l, this, System.currentTimeMillis(), true, true);
    }
}