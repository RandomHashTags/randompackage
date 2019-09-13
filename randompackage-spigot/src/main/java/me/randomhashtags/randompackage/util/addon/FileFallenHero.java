package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.FallenHero;
import me.randomhashtags.randompackage.addon.living.LivingFallenHero;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFallenHero extends RPFallenHero implements FallenHero {
    private ItemStack spawnitem, gem;
    private List<String> summonMsg, receiveKitMsg;

    public FileFallenHero(File f) {
        load(f);
        addFallenHero(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public int getGemDropChance() { return yml.getInt("gem.chance"); }
    public List<String> getSummonMsg() {
        if(summonMsg == null) summonMsg = colorizeListString(yml.getStringList("messages.summon"));
        return summonMsg;
    }
    public List<String> getReceiveKitMsg() {
        if(receiveKitMsg == null) receiveKitMsg = colorizeListString(yml.getStringList("messages.receive kit"));
        return receiveKitMsg;
    }
    public String getType() { return yml.getString("settings.type").toUpperCase(); }
    public List<PotionEffect> getPotionEffects() { return new ArrayList<>(); }
    public ItemStack getSpawnItem() {
        if(spawnitem == null) spawnitem = api.d(yml, "spawn item");
        return getClone(spawnitem);
    }
    public ItemStack getGem() {
        if(gem == null) gem = api.d(yml, "gem");
        return getClone(gem);
    }
    public void spawn(LivingEntity summoner, Location loc, CustomKit kit) {
        if(loc != null && kit != null) {
            new LivingFallenHero(kit, this, summoner != null ? summoner.getUniqueId() : null, loc);
        }
    }
}
