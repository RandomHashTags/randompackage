package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifiable;
import me.randomhashtags.randompackage.addons.living.LivingConquestChest;
import me.randomhashtags.randompackage.addons.objects.ConquestMob;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;

public interface ConquestChest extends Identifiable {
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
    List<String> getRewards();
    List<String> getHitAttributes();
    List<String> getSpawnMsg();
    List<String> getWillSpawnMsg();
    List<String> getStillAliveMsg();
    List<String> getHealthMsg();
    List<String> getUnlockedMsg();
    LivingConquestChest spawn(Location l);
}
