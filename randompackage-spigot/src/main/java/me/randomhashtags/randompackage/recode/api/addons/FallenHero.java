package me.randomhashtags.randompackage.recode.api.addons;

import me.randomhashtags.randompackage.recode.RPAddon;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public abstract class FallenHero extends RPAddon {
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
                if(k.getClass().isInstance(type) && ((FallenHero) k).getSpawnItem().isSimilar(is)) {
                    return (FallenHero) k;
                }
            }
        }
        return null;
    }
    public static FallenHero valueOfFallenHeroGem(ItemStack is, Class type) {
        if(is != null && kits != null) {
            for(CustomKit k : kits.values()) {
                if(k.getClass().isInstance(type)) {
                    final ItemStack i = ((FallenHero) k).getGem();
                    if(i.isSimilar(is)) {
                        return (FallenHero) k;
                    }
                }
            }
        }
        return null;
    }
}
