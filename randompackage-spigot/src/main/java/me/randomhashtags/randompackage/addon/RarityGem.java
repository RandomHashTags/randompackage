package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public interface RarityGem extends Itemable, GivedpItemable {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "raritygem" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final String[] values = originalInput.split(":");
        final RarityGem gem = getRarityGem(values[1]);
        final String three = values.length == 3 ? values[2] : null;
        final int min = three != null ? Integer.parseInt(three.contains("-") ? three.split("-")[0] : three) : 0;
        final int amount = three != null && three.contains("-") ? min+RANDOM.nextInt(Integer.parseInt(three.split("-")[1])-min+1) : min;
        final ItemStack target = gem != null ? gem.getItem(amount) : null;
        return target != null ? target : AIR;
    }

    ItemStack getItem(int souls);
    List<EnchantRarity> getWorksFor();
    List<String> getSplitMsg();
    long getTimeBetweenSameKills();
    HashMap<Integer, String> getColors();
    List<String> getToggleOnMsg();
    List<String> getToggleOffInteractMsg();
    List<String> getToggleOffDroppedMsg();
    List<String> getToggleOffMovedMsg();
    List<String> getToggleOffRanOutMsg();
    String getColors(int soulsCollected);
}
