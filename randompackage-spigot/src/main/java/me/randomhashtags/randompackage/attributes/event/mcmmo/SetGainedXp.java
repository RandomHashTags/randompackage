package me.randomhashtags.randompackage.attributes.event.mcmmo;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.randomhashtags.randompackage.attributes.AbstractEventAttribute;
import org.bukkit.event.Event;

public class SetGainedXp extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        final McMMOPlayerXpGainEvent e = event instanceof McMMOPlayerXpGainEvent ? (McMMOPlayerXpGainEvent) event : null;
        if(e != null) {
            e.setRawXpGained((float) evaluate(value.replace("xp", Float.toString(e.getRawXpGained()))));
        }
    }
}
