package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.event.lootbag.LootbagClaimEvent;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetRewardSize extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof LootbagClaimEvent) {
            final LootbagClaimEvent e = (LootbagClaimEvent) event;
            e.setRewardSize((int) evaluate(replaceValue(value, valueReplacements)));
        }
    }
}
