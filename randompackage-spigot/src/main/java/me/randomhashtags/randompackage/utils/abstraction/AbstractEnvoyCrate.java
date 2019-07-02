package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public abstract class AbstractEnvoyCrate extends AbstractRPFeature {
    public static HashMap<String, AbstractEnvoyCrate> crates;
    public static String defaultTier;

    public void created(String identifier) {
        if(crates == null) crates = new HashMap<>();
        crates.put(identifier, this);
    }
    public abstract Firework getFirework();
    public abstract int getChance();
    public abstract UMaterial getBlock();
    public abstract boolean canRepeatRewards();
    public abstract boolean dropsFromSky();
    public abstract UMaterial getFallingBlock();
    public abstract String getRewardSize();
    public abstract List<UMaterial> cannotLandAbove();
    public abstract ItemStack getItem();
    public abstract List<String> getRewards();
    public abstract List<String> getRandomRewards();
    public abstract List<ItemStack> getRandomizedRewards();
    public abstract boolean canLand(Location l);

    public static AbstractEnvoyCrate valueOf(ItemStack is) {
        if(crates != null && is != null && is.hasItemMeta())
            for(AbstractEnvoyCrate c : crates.values())
                if(is.isSimilar(c.getItem()))
                    return c;
        return null;
    }
    public static AbstractEnvoyCrate getRandomCrate(boolean useChances) {
        if(crates != null) {
            final Random random = new Random();
            if(useChances) {
                for(AbstractEnvoyCrate c : crates.values())
                    if(random.nextInt(100) <= c.getChance())
                        return c;
            } else {
                return crates.get(defaultTier);
            }
            return crates.get(defaultTier);
        }
        return null;
    }
    public static void deleteAll() {
        crates = null;
        defaultTier = null;
    }
}
