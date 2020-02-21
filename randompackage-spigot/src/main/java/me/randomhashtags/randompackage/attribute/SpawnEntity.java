package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.addon.obj.CustomEnchantEntity;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class SpawnEntity extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<String, Entity> entities = pending.getEntities(), keyEntities = pending.getKeyEntities();
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            spawnentity(recipientValues.get(e), entities, keyEntities);
        }
    }
    private void spawnentity(String value, HashMap<String, Entity> entities, HashMap<String, Entity> keyEntities) {
        final String[] values = value.split(":");
        final int amount = Integer.parseInt(values[1]);
        final Entity entity = entities.get(values[2]);
        final LivingEntity target = entity instanceof LivingEntity ? (LivingEntity) entity : null;
        final CustomEnchantEntity cee = CustomEnchantEntity.paths.getOrDefault(values[0], null);
        if(cee != null) {
            for(String s : keyEntities.keySet()) {
                final Entity summoner = keyEntities.get(s);
                if(summoner instanceof LivingEntity) {
                    for(int i = 1; i <= amount; i++) {
                        cee.spawn((LivingEntity) summoner, target);
                    }
                }
            }
        }
    }
}
