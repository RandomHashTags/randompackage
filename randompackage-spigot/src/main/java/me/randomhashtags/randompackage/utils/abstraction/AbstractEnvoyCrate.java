package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractEnvoyCrate extends AbstractRPFeature {
    private UMaterial block, fallingblock;
    private List<UMaterial> cannotLandAbove;
    private ItemStack item;
    private Firework fw;

    public Firework getFirework() {
        if(fw == null) {
            final String[] f = yml.getString("firework").split(":");
            fw = api.createFirework(FireworkEffect.Type.valueOf(f[0].toUpperCase()), api.getColor(f[1]), api.getColor(f[2]), Integer.parseInt(f[3]));
        }
        return fw;
    }
    public int getChance() { return yml.getInt("chance"); }
    public UMaterial getBlock() {
        if(block == null) block = UMaterial.match(yml.getString("settings.block"));
        return block;
    }
    public boolean canRepeatRewards() { return yml.getBoolean("settings.can repeat rewards"); }
    public boolean dropsFromSky() { return yml.getBoolean("settings.drops from sky"); }
    public UMaterial getFallingBlock() {
        if(fallingblock == null) fallingblock = UMaterial.match(yml.getString("settings.falling block"));
        return fallingblock;
    }
    public String getRewardSize() { return yml.getString("settings.reward size"); }
    public List<UMaterial> cannotLandAbove() {
        if(cannotLandAbove == null) {
            cannotLandAbove = new ArrayList<>();
            for(String s : yml.getStringList("settings.cannot land above")) {
                cannotLandAbove.add(UMaterial.match(s));
            }
        }
        return cannotLandAbove;
    }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return item.clone();
    }
    public List<String> getRewards() {
        return yml.getStringList("rewards");
    }

    public int getRandomRewardSize() {
        final String rewardSize = getRewardSize();
        final String[] s = rewardSize.split("-");
        final boolean c = rewardSize.contains("-");
        final int min = c ? Integer.parseInt(s[0]) : Integer.parseInt(rewardSize), max = c ? Integer.parseInt(s[1]) : -1;
        return min+(max == -1 ? 0 : new Random().nextInt(max-min+1));
    }
    public List<String> getRandomRewards() {
        final List<String> rewards = new ArrayList<>(this.getRewards()), actualrewards = new ArrayList<>();
        final Random random = new Random();
        final boolean canRepeatRewards = canRepeatRewards();
        for(int i = 1; i <= getRandomRewardSize(); i++) {
            if(rewards.size() != 0) {
                final String reward = rewards.get(random.nextInt(rewards.size()));
                final String[] a = reward.split(";chance=");
                if(random.nextInt(100) <= api.getRemainingInt(a[1])) {
                    actualrewards.add(a[0]);
                    if(!canRepeatRewards) rewards.remove(reward);
                } else {
                    i -= 1;
                }
            }
        }
        return actualrewards;
    }
    public List<ItemStack> getRandomizedRewards() {
        final List<String> r = getRandomRewards();
        final List<ItemStack> a = new ArrayList<>();
        for(String s : r) {
            final ItemStack i = api.d(null, s);
            if(i != null && !i.getType().equals(Material.AIR)) a.add(i);
        }
        return a;
    }
    public boolean canLand(Location spawnLocation) {
        final World w = spawnLocation.getWorld();
        final Block b = w.getBlockAt(new Location(w, spawnLocation.getBlockX(), spawnLocation.getBlockY()-1, spawnLocation.getBlockZ()));
        if(cannotLandAbove().contains(UMaterial.match(b.getType().name()))) return false;
        return true;
    }
}
