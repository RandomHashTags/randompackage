package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;

public interface Title extends Itemable, GivedpItemable {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "title" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final String[] values = originalInput.split(":");
        final boolean isTitle = originalInput.contains(":");
        Title title = getTitle(isTitle ? values[1] : "random");
        if(title == null) {
            try {
                title = getTitle((String) getAll(Feature.TITLE).keySet().toArray()[getRemainingInt(isTitle ? originalInput.split(":")[1] : originalInput)-1]);
            } catch (Exception e) {
                System.out.println("[RandomPackage] That title doesn't exist!");
            }
        }
        final ItemStack target = title != null ? title.getItem() : null;
        return target != null ? target : AIR;
    }

    String getChatTitle();
    String getTabTitle();
}
