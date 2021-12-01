package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.addon.RandomizationScroll;
import me.randomhashtags.randompackage.addon.Scroll;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class RandomizationScrollUseEvent extends ScrollUseEvent {
    private final CustomEnchantSpigot enchant;
    private final int enchantlevel;
    private int newSuccess, newDestroy;
    public RandomizationScrollUseEvent(Player player, Scroll scroll, ItemStack usedOnItem, CustomEnchantSpigot enchant, int enchantlevel, int newSuccess, int newDestroy) {
        super(player, scroll, usedOnItem);
        this.enchant = enchant;
        this.enchantlevel = enchantlevel;
        this.newSuccess = newSuccess;
        this.newDestroy = newDestroy;
    }
    @Override
    public RandomizationScroll getScroll() {
        return (RandomizationScroll) scroll;
    }
    public CustomEnchantSpigot getEnchant() {
        return enchant;
    }
    public int getEnchantLevel() {
        return enchantlevel;
    }

    public int getNewSuccess() {
        return newSuccess;
    }
    public void setNewSuccess(int success) {
        newSuccess = success;
    }
    public int getNewDestroy() {
        return newDestroy;
    }
    public void setNewDestroy(int destroy) {
        newDestroy = destroy;
    }
}
