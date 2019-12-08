package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.living.LivingConquestChest;
import me.randomhashtags.randompackage.addon.obj.ConquestMob;
import me.randomhashtags.randompackage.addon.util.Rewardable;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;

public interface ConquestChest extends Identifiable, Rewardable {
    String getSpawnRegion();
    String getRewardSize();
    HashMap<ConquestMob, String> getSpawnedBosses();
    int getSpawnInterval();
    int getHealthMsgRadius();
    int getMaxHP();
    int getSpawnedHP();
    int getDamagePerHit();
    int getFirstAnnounced();
    int getAnnounceIntervalAfterSpawned();
    int getDespawnDelay();
    double getDamageDelay();
    UMaterial getPlacedBlock();
    List<String> getHitAttributes();
    List<String> getSpawnMsg();
    List<String> getWillSpawnMsg();
    List<String> getStillAliveMsg();
    List<String> getHealthMsg();
    List<String> getUnlockedMsg();
    LivingConquestChest spawn(Location l);

    double getBossMaxDistanceFromConquest();
    int getBossDistanceCheckInterval();
}
