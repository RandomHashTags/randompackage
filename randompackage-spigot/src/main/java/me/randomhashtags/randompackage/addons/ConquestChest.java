package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifyable;
import me.randomhashtags.randompackage.addons.active.LivingConquestChest;
import me.randomhashtags.randompackage.addons.objects.ConquestMob;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;

public abstract class ConquestChest extends Identifyable {
    public abstract String getSpawnRegion();
    public abstract String getRewardSize();
    public abstract HashMap<ConquestMob, String> getSpawnedBosses();
    public abstract int getSpawnInterval();
    public abstract int getHealthMsgRadius();
    public abstract int getMaxHP();
    public abstract int getSpawnedHP();
    public abstract int getDamagePerHit();
    public abstract int getFirstAnnounced();
    public abstract int getAnnounceIntervalAfterSpawned();
    public abstract int getDespawnDelay();
    public abstract double getDamageDelay();
    public abstract UMaterial getPlacedBlock();
    public abstract List<String> getRewards();
    public abstract List<String> getHitAttributes();
    public abstract List<String> getSpawnMsg();
    public abstract List<String> getWillSpawnMsg();
    public abstract List<String> getStillAliveMsg();
    public abstract List<String> getHealthMsg();
    public abstract List<String> getUnlockedMsg();
    public abstract LivingConquestChest spawn(Location l);
}
