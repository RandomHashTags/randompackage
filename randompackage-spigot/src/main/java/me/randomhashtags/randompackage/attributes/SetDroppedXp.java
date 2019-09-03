package me.randomhashtags.randompackage.attributes;

import me.randomhashtags.randompackage.utils.addons.AbstractEventAttribute;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

public class SetDroppedXp extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        if(event instanceof EntityDeathEvent) {
            final EntityDeathEvent e = (EntityDeathEvent) event;
            e.setDroppedExp((int) evaluate(value.replace("xp", Integer.toString(e.getDroppedExp()))));
        }
    }
}
