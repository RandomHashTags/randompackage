package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Applyable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SoulTracker extends Applyable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "soultracker" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final SoulTracker tracker = getSoulTracker(originalInput.split(":")[1]);
        return tracker != null ? tracker.getItem() : null;
    }

    String getTracks();
    @NotNull List<String> getAppliesTo();
    String getSoulsPerKill();
    double getSoulsCollected();
    RarityGem getConvertsTo();
    List<String> getApplyMsg();
    List<String> getSplitMsg();
}
