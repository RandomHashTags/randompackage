package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.obj.KitItem;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.MaxLevelable;
import me.randomhashtags.randompackage.addon.util.Slotable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface CustomKit extends Itemable, MaxLevelable, Slotable {
    @NotNull Kits getKitClass();
    long getCooldown();
    List<KitItem> getItems();
    @Nullable FallenHero getFallenHero();
    @Nullable default String getFallenHeroName() {
        final FallenHero f = getFallenHero();
        return f != null ? f.getSpawnItem().getItemMeta().getDisplayName().replace("{NAME}", getItem().getItemMeta().getDisplayName()) : null;
    }
    @Nullable
    default ItemStack getFallenHeroItem(@NotNull CustomKit kit, boolean isSpawnItem) {
        final FallenHero hero = kit.getFallenHero();
        final ItemStack item = hero != null ? isSpawnItem ? hero.getSpawnItem() : hero.getGem() : null;
        if(item != null) {
            final String n = kit.getItem().getItemMeta().getDisplayName();
            final ItemMeta m = item.getItemMeta();
            if(m.hasDisplayName()) {
                m.setDisplayName(m.getDisplayName().replace("{NAME}", n));
            }
            final List<String> l = new ArrayList<>();
            if(m.hasLore()) {
                for(String s : m.getLore()) {
                    l.add(s.replace("{NAME}", n));
                }
            }
            m.setLore(l);
            item.setItemMeta(m);
        }
        return item;
    }
}
