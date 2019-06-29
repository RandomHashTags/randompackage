package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.classes.living.LivingConquestChest;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class ConquestChest {
    public static HashMap<String, ConquestChest> types;

    public YamlConfiguration yml;
    public String ymlName;

    private String spawnRegion, rewardSize;
    private HashMap<ConquestMob, String> spawnedBosses;
    private int spawnInterval, healthMsgRadius, maxHP, spawnedHP, dmgPerHit, firstAnnounced, announceIntervalAfterSpawned, despawnDelay;
    private double dmgDelay;
    private UMaterial placedBlock;
    private List<String> rewards, hitAttributes, spawnMsg, willSpawnMsg, stillAliveMsg, healthMsg, unlockedMsg;

    public ConquestChest(YamlConfiguration yml, String ymlName) {
        if(types == null) {
            types = new HashMap<>();
        }
        this.yml = yml;
        this.ymlName = ymlName;

        spawnInterval = yml.getInt("settings.spawn interval");
        maxHP = yml.getInt("settings.max hp");
        spawnedHP = yml.getInt("settings.spawned hp");
        dmgPerHit = yml.getInt("settings.dmg per hit");
        healthMsgRadius = yml.getInt("messages.health radius");
        firstAnnounced = yml.getInt("messages.first announced");
        announceIntervalAfterSpawned = yml.getInt("messages.announce interval after spawned");
        despawnDelay = yml.getInt("messages.despawn delay");
        dmgDelay = yml.getDouble("settings.dmg delay");

        types.put(ymlName, this);
    }
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
    public int getSpawnInterval() { return spawnInterval; }
    public int getHealthMsgRadius() { return healthMsgRadius; }
    public int getMaxHP() { return maxHP; }
    public int getSpawnedHP() { return spawnedHP; }
    public int getDamagePerHit() { return dmgPerHit; }
    public int getFirstAnnounced() { return firstAnnounced; }
    public int getAnnounceIntervalAfterSpawned() { return announceIntervalAfterSpawned; }
    public int getDespawnDelay() { return despawnDelay; }
    public double getDamageDelay() { return dmgDelay; }
    public UMaterial getPlacedBlock() {
        if(placedBlock == null) placedBlock = UMaterial.match(yml.getString("settings.placed block").toUpperCase());
        return placedBlock;
    }
    public List<String> getRewards() {
        if(rewards == null) rewards = yml.getStringList("rewards");
        return rewards;
    }
    public List<String> getHitAttributes() {
        if(hitAttributes == null) hitAttributes = yml.getStringList("hit attributes");
        return hitAttributes;
    }
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

    public static void deleteAll() {
        types = null;
    }
}