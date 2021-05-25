package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.data.CustomEnchantData;

import java.util.List;

public final class CustomEnchantDataObj implements CustomEnchantData {
    private final List<LivingCustomEnchantEntity> entities;

    public CustomEnchantDataObj(List<LivingCustomEnchantEntity> entities) {
        this.entities = entities;
    }

    @Override
    public List<LivingCustomEnchantEntity> getEntities() {
        return entities;
    }
}
