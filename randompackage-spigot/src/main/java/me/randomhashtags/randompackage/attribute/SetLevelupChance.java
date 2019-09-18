package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.event.kit.KitClaimEvent;
import org.bukkit.event.Event;

public class SetLevelupChance extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        if(event instanceof KitClaimEvent) {
            final KitClaimEvent ev = (KitClaimEvent) event;
            ev.setLevelupChance((int) evaluate(value));
        }
    }
}
