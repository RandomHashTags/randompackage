package me.randomhashtags.randompackage.attributes.handlers;

import me.randomhashtags.randompackage.attributes.AbstractEventHandler;
import me.randomhashtags.randompackage.attributes.Combo;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class HandleCombo extends AbstractEventHandler implements Combo {
    @Override
    public void handle(Event event, String value) {
        if(event instanceof EntityDamageEvent) {
            final EntityDamageEvent e = (EntityDamageEvent) event;
            final double damage = e.getDamage();
            handleCombo(e, damage, e.getEntity().getUniqueId(), value);
            if(e instanceof EntityDamageByEntityEvent) {
                final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
                handleCombo(ee, damage, ee.getDamager().getUniqueId(), value);
            }
        }
    }
    private void handleCombo(EntityDamageEvent event, double damage, UUID entity, String value) {
        if(combos.containsKey(entity)) {
            event.setDamage(damage*combos.get(entity).getOrDefault(value, 1.00));
        }
    }
}
