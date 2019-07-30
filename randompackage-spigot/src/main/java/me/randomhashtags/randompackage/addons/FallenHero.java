package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Spawnable;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public interface FallenHero extends Spawnable {
    ItemStack getSpawnItem();
    ItemStack getGem();
    List<PotionEffect> getPotionEffects();
    int getGemDropChance();
    List<String> getSummonMsg();
    List<String> getReceiveKitMsg();
    String getType();
    void spawn(LivingEntity summoner, Location loc, CustomKit kit);
}
