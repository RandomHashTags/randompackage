package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public abstract class EnvoyCrate extends Itemable {
    protected static String defaultTier;

    public abstract Firework getFirework();
    public abstract int getChance();
    public abstract UMaterial getBlock();
    public abstract boolean canRepeatRewards();
    public abstract boolean dropsFromSky();
    public abstract UMaterial getFallingBlock();
    public abstract String getRewardSize();
    public abstract List<UMaterial> cannotLandAbove();
    public abstract List<String> getRewards();
    public abstract List<String> getRandomRewards();
    public abstract List<ItemStack> getRandomizedRewards();
    public abstract boolean canLand(Location l);

    public static EnvoyCrate valueOf(ItemStack is) {
        if(envoycrates != null && is != null && is.hasItemMeta())
            for(EnvoyCrate c : envoycrates.values())
                if(is.isSimilar(c.getItem()))
                    return c;
        return null;
    }
    public static EnvoyCrate getRandomCrate(boolean useChances) {
        if(envoycrates != null) {
            final Random random = new Random();
            if(useChances) {
                for(EnvoyCrate c : envoycrates.values())
                    if(random.nextInt(100) <= c.getChance())
                        return c;
            } else {
                return envoycrates.get(defaultTier);
            }
            return envoycrates.get(defaultTier);
        }
        return null;
    }
}
