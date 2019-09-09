package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface EnchantRarity extends Identifiable {
    String[] getRevealedEnchantRarities();
    List<String> getRevealedEnchantMsg();
    ItemStack getRevealItem();
    ItemStack getRevealedItem();
    String getNameColors();
    String getApplyColors();
    boolean percentsAddUpto100();
    String getSuccess();
    String getDestroy();
    List<String> getLoreFormat();
    int getSuccessSlot();
    int getDestroySlot();
    Firework getFirework();
    List<CustomEnchant> getEnchants();
}
