package me.randomhashtags.randompackage.utils.abstraction;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractFallenHero extends Spawnable {
    public static HashMap<NamespacedKey, AbstractFallenHero> heroes;

    public void created(NamespacedKey key) {
        if(heroes == null) heroes = new HashMap<>();
        heroes.put(key, this);
    }

    public abstract int getGemDropChance();
    public abstract List<String> getSummonMsg();
    public abstract List<String> getReceiveKitMsg();
    public abstract String getType();
    public abstract String getName();
    public abstract List<PotionEffect> getPotionEffects();
    public abstract ItemStack getSpawnItem();
    public abstract ItemStack getGem();
    public abstract void spawn(LivingEntity summoner, Location loc, AbstractCustomKit kit);

    public static AbstractFallenHero valueOf(ItemStack spawnitem) {
        if(heroes != null && spawnitem != null && spawnitem.hasItemMeta())
            for(AbstractFallenHero h : heroes.values())
                if(h.getSpawnItem().isSimilar(spawnitem)) return h;
        return null;
    }
    public static AbstractFallenHero valueOF(ItemStack gem) {
        if(heroes != null && gem != null && gem.hasItemMeta())
            for(AbstractFallenHero h : heroes.values())
                if(h.getGem().isSimilar(gem)) return h;
        return null;
    }
    public static void deleteAll() {
        heroes = null;
    }
}
