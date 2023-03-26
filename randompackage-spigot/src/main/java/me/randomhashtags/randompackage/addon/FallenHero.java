package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Spawnable;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface FallenHero extends Spawnable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "fallenherogem", "fallenhero" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final boolean isSpawnItem = !lowercaseInput.startsWith("fallenherogem");
        final String type = originalInput.contains(":") ? originalInput.split(":")[1] : null;
        CustomKit kit = type != null ? getCustomKit(type) : null;
        final Collection<CustomKit> kits = getAllCustomKits().values();
        if(type != null && kit == null) {
            final List<CustomKit> list = new ArrayList<>();
            for(CustomKit targetKit : kits) {
                if(targetKit.getIdentifier().startsWith(type)) {
                    list.add(targetKit);
                }
            }
            final int size = list.size();
            if(size > 0) {
                kit = list.get(RANDOM.nextInt(size));
            }
        }
        if(kit == null) {
            kit = (CustomKit) kits.toArray()[RANDOM.nextInt(kits.size())];
        }
        final FallenHero f = kit != null ? kit.getFallenHero() : null;
        return f != null ? kit.getFallenHeroItem(kit, isSpawnItem) : AIR;
    }

    @NotNull ItemStack getSpawnItem();
    @NotNull ItemStack getGem();
    List<PotionEffect> getPotionEffects();
    int getGemDropChance();
    List<String> getSummonMsg();
    List<String> getReceiveKitMsg();
    String getType();
    void spawn(LivingEntity summoner, Location loc, CustomKit kit);
}
