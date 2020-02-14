package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Applyable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface SoulTracker extends Applyable, GivedpItemable {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "soultracker" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final SoulTracker tracker = getSoulTracker(originalInput.split(":")[1]);
        final ItemStack target = tracker != null ? tracker.getItem() : null;
        return target != null ? target : AIR;
    }

    String getTracks();
    List<String> getAppliesTo();
    String getSoulsPerKill();
    double getSoulsCollected();
    RarityGem getConvertsTo();
    List<String> getApplyMsg();
    List<String> getSplitMsg();
}
