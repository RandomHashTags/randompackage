package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.event.kit.KitPreClaimEvent;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetLevelupChance extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof KitPreClaimEvent) {
            final KitPreClaimEvent ev = (KitPreClaimEvent) event;
            ev.setLevelupChance((int) evaluate(replaceValue(value, valueReplacements)));
        }
    }
}
