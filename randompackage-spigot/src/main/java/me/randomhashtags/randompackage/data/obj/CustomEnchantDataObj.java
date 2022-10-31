package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.data.CustomEnchantData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CustomEnchantDataObj implements CustomEnchantData {
    private final List<LivingCustomEnchantEntity> entities;

    public CustomEnchantDataObj(@NotNull List<LivingCustomEnchantEntity> entities) {
        this.entities = entities;
    }

    @NotNull
    @Override
    public List<LivingCustomEnchantEntity> getEntities() {
        return entities;
    }
}
