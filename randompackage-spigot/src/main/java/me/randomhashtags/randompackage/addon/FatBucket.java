package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public interface FatBucket extends Itemable, RPItemStack, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "fatbucket" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final String[] values = originalInput.split(":");
        final FatBucket bucket = getFatBucket(values[1]);
        final ItemStack target = bucket != null ? values.length > 2 ? bucket.getItem(Integer.parseInt(values[1])) : bucket.getItem(0) : null;
        return target != null ? target : AIR;
    }

    default String getValues(ItemStack is) {
        return getRPItemStackValue(is, "FatBucketInfo");
    }
    default ItemStack getItem(int usesLeft) {
        return getItem(usesLeft, 0, true);
    }
    default ItemStack getItem(int usesLeft, int sourcesRequired, boolean updateMetadata) {
        final ItemStack is;
        final ItemMeta meta;
        final boolean isUsing = usesLeft > 0;
        if(isUsing) {
            is = new ItemStack(Material.LAVA_BUCKET);
            meta = getItem().getItemMeta();
        } else {
            is = getItem();
            meta = is.getItemMeta();
        }
        final String statusPercent = isUsing ? getUsesLeftStatus() : getPercentFullStatus();
        final int percent = (int) ((((double) sourcesRequired)/((double) getSourcesRequired()))*100);
        meta.setDisplayName(meta.getDisplayName() + statusPercent.replace("{USES}", Integer.toString(usesLeft)).replace("{PERCENT}", Integer.toString(percent)));
        is.setItemMeta(meta);
        if(updateMetadata) {
            setItem(is, usesLeft, sourcesRequired);
        }
        return is;
    }
    default void setItem(ItemStack is, int usesLeft, int sourcesRequired) {
        final ItemStack i = getItem(usesLeft, sourcesRequired, false);
        final Material first = i.getType(), second = is.getType();
        if(first != second) {
            is.setType(first);
        }
        final ItemMeta meta = i.getItemMeta();
        is.setItemMeta(meta);
        addRPItemStackValues(is, new HashMap<String, String>() {{
            put("FatBucketInfo", getIdentifier() + ":" + usesLeft + ":" + sourcesRequired);
        }});
    }
    default void didPlace(ItemStack is) {
        final String[] info = getValues(is).split(":");
        final int usesLeft = Integer.parseInt(info[1]);
        setItem(is, usesLeft-1, 0);
    }
    default void didFill(ItemStack is) {
        final String[] info = getValues(is).split(":");
        final int sourcesRequired = Integer.parseInt(info[2]);
        final boolean isFull = sourcesRequired+1 == getSourcesRequired();
        setItem(is, isFull ? getUses() : 0, sourcesRequired+1);
    }

    int getUses();
    int getSourcesRequired();
    String getUsesLeftStatus();
    String getPercentFullStatus();
    List<String> getEnabledWorlds();
    List<String> getFillableInWorlds();
    List<String> getOnlyFillableInWorldsMsg();
}
