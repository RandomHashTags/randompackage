package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.living.LivingCustomBoss;
import me.randomhashtags.randompackage.addon.obj.CustomBossAttack;
import me.randomhashtags.randompackage.addon.obj.CustomMinion;
import me.randomhashtags.randompackage.addon.util.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public interface CustomBoss extends RPEntity, Scoreboardable, Spawnable {
    ItemStack getSpawnItem();
    List<CustomBossAttack> getAttacks();
    HashMap<Integer, List<String>> getMessages();
    int getMessageRadius();
    int getMaxMinions();
    CustomMinion getMinion();
    default LivingCustomBoss spawn(LivingEntity summoner, Location location) {
        return new LivingCustomBoss(summoner, api.getEntity(getType(), location, true), this);
    }
}
