package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.addon.living.LivingCustomBoss;
import me.randomhashtags.randompackage.addon.obj.CustomBossAttack;
import me.randomhashtags.randompackage.addon.obj.CustomMinion;
import me.randomhashtags.randompackage.addon.util.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public interface CustomBoss extends RPEntity, Scoreboardable, Spawnable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "customboss" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final CustomBoss boss = getCustomBoss(originalInput.split(":")[1]);
        return boss != null ? boss.getSpawnItem() : null;
    }

    @NotNull ItemStack getSpawnItem();
    @NotNull List<CustomBossAttack> getAttacks();
    @NotNull HashMap<Integer, List<String>> getMessages();
    int getMessageRadius();
    int getMaxMinions();
    @Nullable CustomMinion getMinion();

    @NotNull
    default LivingCustomBoss spawn(LivingEntity summoner, Location location) {
        return new LivingCustomBoss(summoner, RandomPackageAPI.INSTANCE.getEntity(getType(), location, true), this);
    }
}
