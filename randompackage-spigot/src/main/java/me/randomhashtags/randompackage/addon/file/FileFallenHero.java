package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.FallenHero;
import me.randomhashtags.randompackage.addon.living.LivingFallenHero;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileFallenHero extends RPFallenHeroSpigot implements FallenHero {
    private ItemStack spawnitem, gem;

    public FileFallenHero(File f) {
        super(f);
        register(Feature.FALLEN_HERO, this);
    }

    public int getGemDropChance() { return yml.getInt("gem.chance"); }
    public List<String> getSummonMsg() { return getStringList(yml, "messages.summon"); }
    public List<String> getReceiveKitMsg() { return getStringList(yml, "messages.receive kit"); }
    public String getType() { return yml.getString("settings.type").toUpperCase(); }
    public List<PotionEffect> getPotionEffects() { return new ArrayList<>(); }
    @NotNull
    public ItemStack getSpawnItem() {
        if(spawnitem == null) spawnitem = createItemStack(yml, "spawn item");
        return getClone(spawnitem);
    }
    @NotNull
    public ItemStack getGem() {
        if(gem == null) gem = createItemStack(yml, "gem");
        return getClone(gem);
    }
    public void spawn(LivingEntity summoner, Location loc, CustomKit kit) {
        if(loc != null && kit != null) {
            new LivingFallenHero(kit, this, summoner != null ? summoner.getUniqueId() : null, loc);
        }
    }
}
