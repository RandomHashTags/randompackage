package me.randomhashtags.randompackage.data;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.RarityGem;

import java.util.HashMap;

public interface RarityGemData {
    HashMap<RarityGem, Boolean> getRarityGems();
    default boolean isActive(@NotNull RarityGem gem) {
        final HashMap<RarityGem, Boolean> gems = getRarityGems();
        return gems != null && gems.containsKey(gem) && gems.get(gem);
    }
    void toggle(@NotNull RarityGem gem);
}
