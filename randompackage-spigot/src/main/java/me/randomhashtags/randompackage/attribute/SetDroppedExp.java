package me.randomhashtags.randompackage.attribute;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;

public class SetDroppedExp extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value, HashMap<String, String> valueReplacements) {
        switch (event.getEventName().toLowerCase().split("event")[0]) {
            case "entitydeath":
                int amount = (int) evaluate(replaceValue(value, valueReplacements));
                ((EntityDeathEvent) event).setDroppedExp(amount);
                break;
            case "blockbreak":
                amount = (int) evaluate(replaceValue(value, valueReplacements));
                ((BlockBreakEvent) event).setExpToDrop(amount);
                break;
            default:
                break;
        }
    }
}
