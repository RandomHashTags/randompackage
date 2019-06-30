package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.NamespacedKey;
import me.randomhashtags.randompackage.utils.classes.custombosses.CustomBossAttack;
import me.randomhashtags.randompackage.utils.classes.custombosses.CustomMinion;
import me.randomhashtags.randompackage.utils.classes.living.LivingCustomBoss;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractCustomBoss extends Spawnable {
    public static HashMap<NamespacedKey, AbstractCustomBoss> bosses;

    public void created(NamespacedKey key) {
        if(bosses == null) bosses = new HashMap<>();
        bosses.put(key, this);
    }

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

    public static AbstractCustomBoss valueOf(ItemStack spawnitem) {
        if(bosses != null && spawnitem != null && spawnitem.hasItemMeta())
            for(AbstractCustomBoss c : bosses.values())
                if(c.getSpawnItem().getItemMeta().equals(spawnitem.getItemMeta()))
                    return c;
        return null;
    }
}
