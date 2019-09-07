package me.randomhashtags.randompackage.attributes;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class SetDroppedExp extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        if(event instanceof EntityDeathEvent) {
            final EntityDeathEvent e = (EntityDeathEvent) event;
            e.setDroppedExp((int) evaluate(value));
        } else if(event instanceof BlockBreakEvent) {
            final BlockBreakEvent e = (BlockBreakEvent) event;
            e.setExpToDrop((int) evaluate(value));
        }
    }
}
