package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EnchantRarity extends Identifiable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "raritybook" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final EnchantRarity rarity = getCustomEnchantRarity(originalInput.split(":")[1]);
        return rarity != null ? rarity.getRevealItem() : null;
    }

    String[] getRevealedEnchantRarities();
    List<String> getRevealedEnchantMsg();
    ItemStack getRevealItem();
    @NotNull ItemStack getRevealedItem();
    String getNameColors();
    String getApplyColors();
    boolean percentsAddUpto100();
    String getSuccess();
    String getDestroy();
    List<String> getLoreFormat();
    int getSuccessSlot();
    int getDestroySlot();
    Firework getFirework();
    List<CustomEnchantSpigot> getEnchants();
}
