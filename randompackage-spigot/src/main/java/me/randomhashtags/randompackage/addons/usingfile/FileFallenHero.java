package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.CustomKit;
import me.randomhashtags.randompackage.addons.FallenHero;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public abstract class FileFallenHero extends FallenHero {
    private ItemStack spawnitem, gem;
    private List<String> summonMsg, receiveKitMsg;

    public int getGemDropChance() { return yml.getInt("gem.chance"); }
    public List<String> getSummonMsg() {
        if(summonMsg == null) summonMsg = api.colorizeListString(yml.getStringList("messages.summon"));
        return summonMsg;
    }
    public List<String> getReceiveKitMsg() {
        if(receiveKitMsg == null) receiveKitMsg = api.colorizeListString(yml.getStringList("messages.receive kit"));
        return receiveKitMsg;
    }
    public String getType() { return yml.getString("settings.type").toUpperCase(); }
    public List<PotionEffect> getPotionEffects() { return new ArrayList<>(); }
    public ItemStack getSpawnItem() {
        if(spawnitem == null) spawnitem = api.d(yml, "spawn item");
        return spawnitem.clone();
    }
    public ItemStack getGem() {
        if(gem == null) gem = api.d(yml, "gem");
        return gem.clone();
    }
    public void spawn(LivingEntity summoner, Location loc, CustomKit kit) {
        if(loc != null && kit != null) {
            //new LivingFallenHero(kit, this, summoner != null ? summoner.getUniqueId() : null, loc);
        }
    }
}
