package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.utils.addons.RPSpawnable;
import me.randomhashtags.randompackage.addons.objects.CustomBossAttack;
import me.randomhashtags.randompackage.addons.objects.CustomMinion;
import me.randomhashtags.randompackage.addons.living.LivingCustomBoss;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.HashMap;
import java.util.List;

public abstract class CustomBoss extends RPSpawnable {
    public abstract String getType();
    public abstract String getName();
    public abstract String getScoreboardTitle();
    public abstract DisplaySlot getScoreboardSlot();
    public abstract List<String> getScoreboardScores();
    public abstract ItemStack getSpawnItem();
    public abstract List<String> getAttributes();
    public abstract List<String> getRewards();
    public abstract List<CustomBossAttack> getAttacks();
    public abstract HashMap<Integer, List<String>> getMessages();
    public abstract int getMessageRadius();
    public abstract int getMaxMinions();
    public abstract CustomMinion getMinion();

    public LivingCustomBoss spawn(LivingEntity summoner, Location location) {
        return new LivingCustomBoss(summoner, api.getEntity(getType(), location, true), this);
    }

    public static CustomBoss valueOf(ItemStack spawnitem) {
        if(bosses != null && spawnitem != null && spawnitem.hasItemMeta()) {
            for(CustomBoss b : bosses.values()) {
                if(b.getSpawnItem().isSimilar(spawnitem)) {
                    return b;
                }
            }
        }
        return null;
    }
}
