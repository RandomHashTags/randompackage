package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;

public final class SetDroppedExp extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, String value, HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getEntities();
        final int amount = getAmount(entities, value, valueReplacements);
        switch (event.getEventName().toLowerCase().split("event")[0]) {
            case "entitydeath":
                ((EntityDeathEvent) event).setDroppedExp(amount);
                break;
            case "blockbreak":
                ((BlockBreakEvent) event).setExpToDrop(amount);
                break;
            default:
                break;
        }
    }
    private int getAmount(HashMap<String, Entity> entities, String value, HashMap<String, String> valueReplacements) {
        return (int) evaluate(replaceValue(entities, value, valueReplacements));
    }
}
