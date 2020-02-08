package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.EnvoyCrate;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileEnvoyCrate extends RPAddon implements EnvoyCrate {
    private UMaterial block, fallingblock;
    private List<UMaterial> cannotLandAbove, cannotLandIn;
    private ItemStack item;
    private Firework fw;

    public FileEnvoyCrate(File f) {
        load(f);
        register(Feature.ENVOY_CRATE, this);
    }
    public String getIdentifier() { return getYamlName(); }

    public Firework getFirework() {
        if(fw == null) {
            final String[] f = yml.getString("firework").split(":");
            fw = createFirework(FireworkEffect.Type.valueOf(f[0].toUpperCase()), getColor(f[1]), getColor(f[2]), Integer.parseInt(f[3]));
        }
        return fw;
    }
    public int getChance() { return yml.getInt("chance"); }
    public UMaterial getBlock() {
        if(block == null) {
            block = UMaterial.match(yml.getString("settings.block"));
        }
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
    public List<UMaterial> cannotLandIn() {
        if(cannotLandIn == null) {
            cannotLandIn = new ArrayList<>();
            for(String s : yml.getStringList("settings.cannot land in")) {
                cannotLandIn.add(UMaterial.match(s));
            }
        }
        return cannotLandIn;
    }
    public ItemStack getItem() {
        if(item == null) item = API.createItemStack(yml, "item");
        return getClone(item);
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
                final boolean hasChance = reward.toLowerCase().contains(";chance=");
                final String[] a = reward.split(";chance=");
                if(!hasChance || random.nextInt(100) <= API.getRemainingInt(a[1])) {
                    actualrewards.add(a[0]);
                    if(!canRepeatRewards) {
                        rewards.remove(reward);
                    }
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
            final ItemStack i = API.createItemStack(null, s);
            if(i != null && !i.getType().equals(Material.AIR)) {
                a.add(i);
            }
        }
        return a;
    }
}
