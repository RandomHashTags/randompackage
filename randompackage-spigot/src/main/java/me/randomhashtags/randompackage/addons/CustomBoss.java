package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.living.LivingCustomBoss;
import me.randomhashtags.randompackage.addons.objects.CustomBossAttack;
import me.randomhashtags.randompackage.addons.objects.CustomMinion;
import me.randomhashtags.randompackage.addons.utils.*;
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
