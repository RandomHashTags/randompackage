package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.data.RarityGemData;

import java.util.HashMap;

public final class RarityGemDataObj implements RarityGemData {
    private final HashMap<RarityGem, Boolean> gems;

    public RarityGemDataObj(HashMap<RarityGem, Boolean> gems) {
        this.gems = gems;
    }

    @Override
    public HashMap<RarityGem, Boolean> getRarityGems() {
        return gems;
    }
}
