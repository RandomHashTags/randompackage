package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.event.BlackScrollUseEvent;
import me.randomhashtags.randompackage.event.RandomizationScrollUseEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetSuccessRate extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, String value, HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getKeyEntities();
        if(value != null) {
            final int percent = (int) evaluate(replaceValue(entities, value, valueReplacements));
            switch (event.getEventName().toLowerCase().split("scrolluseevent")[0]) {
                case "black":
                    final BlackScrollUseEvent bEvent = (BlackScrollUseEvent) event;
                    bEvent.setSuccessRate(percent);
                    break;
                case "randomization":
                    final RandomizationScrollUseEvent rEvent = (RandomizationScrollUseEvent) event;
                    rEvent.setNewSuccess(percent);
                    break;
                default:
                    break;
            }
        }
    }
}
