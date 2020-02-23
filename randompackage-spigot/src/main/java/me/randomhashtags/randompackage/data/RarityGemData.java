package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.RarityGem;

import java.util.HashMap;
import java.util.List;

public interface RarityGemData {
    HashMap<RarityGem, Boolean> getRarityGems();
    default boolean isActive(@NotNull RarityGem gem) {
        final HashMap<RarityGem, Boolean> gems = getRarityGems();
        return gems != null && gems.containsKey(gem) && gems.get(gem);
    }
    default void toggle(@NotNull RarityGem gem, List<String> msg) {
        final HashMap<RarityGem, Boolean> gems = getRarityGems();
        if(gems != null) {
            gems.put(gem, !gems.getOrDefault(gem, false));
        }
    }
}
