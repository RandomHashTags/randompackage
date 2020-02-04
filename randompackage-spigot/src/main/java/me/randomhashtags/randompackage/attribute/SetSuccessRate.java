package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.event.BlackScrollUseEvent;
import me.randomhashtags.randompackage.event.RandomizationScrollUseEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetSuccessRate extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending) {
        final Event event = pending.getEvent();
        final HashMap<Entity, String> values = pending.getRecipientValues();
        final HashMap<String, Entity> entities = pending.getKeyEntities();
        final String value = values.getOrDefault(entities.get("Player"), null);
        if(value != null) {
            switch (event.getEventName().toLowerCase().split("scrolluseevent")[0]) {
                case "black":
                    final BlackScrollUseEvent bEvent = (BlackScrollUseEvent) event;
                    bEvent.setSuccessRate(getPercent(entities, value, bEvent.getSuccessRate()));
                    break;
                case "randomization":
                    final RandomizationScrollUseEvent rEvent = (RandomizationScrollUseEvent) event;
                    rEvent.setNewSuccess(getPercent(entities, value, rEvent.getNewSuccess()));
                    break;
                default:
                    break;
            }
        }
    }
    private int getPercent(HashMap<String, Entity> entities, String value, int rate) {
        final String replacedValue = replaceValue(entities, value, new HashMap<String, String>() {{
            put("rate", Integer.toString(rate));
        }});
        return (int) evaluate(replacedValue);
    }
}
