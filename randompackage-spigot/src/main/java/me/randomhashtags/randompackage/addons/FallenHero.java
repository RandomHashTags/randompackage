package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Spawnable;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public abstract class FallenHero extends Spawnable {
    public abstract ItemStack getSpawnItem();
    public abstract ItemStack getGem();
    public abstract List<PotionEffect> getPotionEffects();
    public abstract int getGemDropChance();
    public abstract List<String> getSummonMsg();
    public abstract List<String> getReceiveKitMsg();
    public abstract String getType();
    public abstract void spawn(LivingEntity summoner, Location loc, CustomKit kit);

    public static FallenHero valueOfFallenHeroSpawnItem(ItemStack is, Class type) {
        if(is != null && kits != null) {
            for(CustomKit k : kits.values()) {
                final ItemStack f = k.getFallenHeroSpawnItem(k);
                if(f != null && k.getClass().isInstance(type) && f.isSimilar(is)) {
                    return k.getFallenHero();
                }
            }
        }
        return null;
    }
    public static FallenHero valueOfFallenHeroGem(ItemStack is, Class type) {
        if(is != null && kits != null) {
            for(CustomKit k : kits.values()) {
                final ItemStack f = k.getFallenHeroGemItem(k);
                if(f != null && k.getClass().isInstance(type) && f.isSimilar(is)) {
                    return k.getFallenHero();
                }
            }
        }
        return null;
    }
}
