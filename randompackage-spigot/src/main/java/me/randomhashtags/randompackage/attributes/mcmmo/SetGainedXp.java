package me.randomhashtags.randompackage.attributes.mcmmo;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.randomhashtags.randompackage.attributes.AbstractEventAttribute;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetGainedXp extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof McMMOPlayerXpGainEvent) {
            final McMMOPlayerXpGainEvent e = (McMMOPlayerXpGainEvent) event;
            e.setRawXpGained((float) evaluate(replaceValue(value, valueReplacements)));
        }
    }
}
