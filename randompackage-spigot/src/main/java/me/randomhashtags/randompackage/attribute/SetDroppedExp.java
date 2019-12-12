package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;

public class SetDroppedExp extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, String value, HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getEntities();
        switch (event.getEventName().toLowerCase().split("event")[0]) {
            case "entitydeath":
                int amount = (int) evaluate(replaceValue(entities, value, valueReplacements));
                ((EntityDeathEvent) event).setDroppedExp(amount);
                break;
            case "blockbreak":
                amount = (int) evaluate(replaceValue(entities, value, valueReplacements));
                ((BlockBreakEvent) event).setExpToDrop(amount);
                break;
            default:
                break;
        }
    }
}
