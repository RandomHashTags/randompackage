package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;

import java.util.List;
import java.util.UUID;

public interface CustomEnchantData {
    List<LivingCustomEnchantEntity> getEntities();
    default boolean containsEntity(@NotNull UUID uuid) {
        for(LivingCustomEnchantEntity entity : getEntities()) {
            if(uuid.equals(entity.getEntity().getUniqueId())) {
                return true;
            }
        }
        return false;
    }
}
