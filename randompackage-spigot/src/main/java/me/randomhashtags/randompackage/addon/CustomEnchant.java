package me.randomhashtags.randompackage.addon;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.MaxLevelable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.addon.util.Toggleable;
import me.randomhashtags.randompackage.event.armor.ArmorEvent;
import me.randomhashtags.randompackage.util.Versionable;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.math.BigDecimal;
import java.util.List;

public interface CustomEnchant extends Attributable, MaxLevelable, Nameable, Toggleable, Versionable {
    List<String> getEnabledInWorlds();
    default boolean canProcInWorld(@NotNull World world) {
        return canProcInWorld(world.getName());
    }
    default boolean canProcInWorld(@NotNull String world) {
        final List<String> worlds = getEnabledInWorlds();
        return worlds != null && worlds.contains(world);
    }
    List<String> getLore();
    List<String> getAppliesTo();
    String getRequiredEnchant();
    BigDecimal[] getAlchemist();
    BigDecimal[] getTinkerer();
    String getEnchantProcValue();

    default boolean isOnCorrectItem(ItemStack is) {
        if(is != null) {
            final String mat = is.getType().name();
            for(String s : getAppliesTo()) {
                if(mat.endsWith(s.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }
    default boolean canBeTriggered(Event event, Player player, ItemStack is) {
        if(event != null && player != null && is != null) {
            final String mat = is.getType().name();
            final ItemStack target;
            switch (event.getEventName().toLowerCase().split("event")[0]) {
                case "armorequip":
                case "armorunequip":
                case "armorpiecebreak":
                    target = ((ArmorEvent) event).getItem();
                    break;
                default:
                    target = null;
                    break;
            }
            final boolean other = is.equals(target);
            if(other) return true;
            final PlayerInventory inv = player.getInventory();
            for(String s : getAppliesTo()) {
                if(mat.endsWith(s.toUpperCase())) {
                    if(mat.contains("HELMET")) {
                        return is.equals(inv.getHelmet());
                    } else if(mat.contains("CHESTPLATE") || mat.equals("ELYTRA")) {
                        return is.equals(inv.getChestplate());
                    } else if(mat.contains("LEGGINGS")) {
                        return is.equals(inv.getLeggings());
                    } else if(mat.contains("BOOTS")) {
                        return is.equals(inv.getBoots());
                    } else {
                        return is.equals(inv.getItemInHand()) || !EIGHT && is.equals(inv.getItemInOffHand());
                    }
                }
            }
        }
        return false;
    }

    default BigDecimal getAlchemistUpgradeCost(int level) {
        final BigDecimal[] i = getAlchemist();
        final int l = level-1;
        return l < i.length ? i[l] : BigDecimal.ZERO;
    }
    default BigDecimal getTinkererValue(int level) {
        final BigDecimal[] i = getTinkerer();
        final int l = level-1;
        return l < i.length ? i[l] : BigDecimal.ZERO;
    }
}
