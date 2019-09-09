package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface CustomKitItem extends Identifiable {
    CustomKit getKit();
    String getItem();
    String getAmount();
    String getName();
    List<String> getLore();
    int getChance();
    int getRequiredLevel();
    default ItemStack getItemStack() { return getItemStack(getKit().getMaxLevel()); }
    default ItemStack getItemStack(int level) { return getItemStack(level, 1.00f); }
    default ItemStack getItemStack(int level, float enchantMultiplier) { return getItemStack("null", level, enchantMultiplier); }
    ItemStack getItemStack(String player, int level, float enchantMultiplier);
}
