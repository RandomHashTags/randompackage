package me.randomhashtags.randompackage.attribute;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;

public class SetDroppedExp extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof EntityDeathEvent) {
            final int amount = (int) evaluate(replaceValue(value, valueReplacements));
            final EntityDeathEvent e = (EntityDeathEvent) event;
            e.setDroppedExp(amount);
        } else if(event instanceof BlockBreakEvent) {
            final int amount = (int) evaluate(replaceValue(value, valueReplacements));
            final BlockBreakEvent e = (BlockBreakEvent) event;
            e.setExpToDrop(amount);
        }
    }
}
