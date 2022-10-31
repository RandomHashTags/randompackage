package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface CustomEnchantData {
    @NotNull List<LivingCustomEnchantEntity> getEntities();
    default boolean containsEntity(@NotNull UUID uuid) {
        for(LivingCustomEnchantEntity entity : getEntities()) {
            if(uuid.equals(entity.getEntity().getUniqueId())) {
                return true;
            }
        }
        return false;
    }
}
