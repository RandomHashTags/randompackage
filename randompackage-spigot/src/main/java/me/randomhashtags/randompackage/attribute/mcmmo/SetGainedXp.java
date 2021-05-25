package me.randomhashtags.randompackage.attribute.mcmmo;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.randomhashtags.randompackage.attribute.AbstractEventAttribute;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public final class SetGainedXp extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, String value, HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getEntities();
        if(event instanceof McMMOPlayerXpGainEvent) {
            final McMMOPlayerXpGainEvent e = (McMMOPlayerXpGainEvent) event;
            e.setRawXpGained((float) evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
