package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.event.armor.ArmorEvent;
import me.randomhashtags.randompackage.util.Versionable;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CustomEnchantSpigot extends CustomEnchant, Versionable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "customenchant", "ce" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final ItemStack target = CustomEnchants.INSTANCE.getRevealedItemFromString(originalInput);
        return target != null ? target : AIR;
    }

    default boolean canProcInWorld(@NotNull World world) {
        return canProcInWorld(world.getName());
    }
    default boolean canProcInWorld(@NotNull String world) {
        return getEnabledInWorlds().contains(world);
    }

    default boolean isOnCorrectItem(@NotNull ItemStack is) {
        final String mat = is.getType().name();
        for(String s : getAppliesTo()) {
            if(mat.endsWith(s.toUpperCase())) {
                return true;
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
}
