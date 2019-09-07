package me.randomhashtags.randompackage.attributes;

import me.randomhashtags.randompackage.addons.GlobalChallenge;
import me.randomhashtags.randompackage.addons.living.ActiveGlobalChallenge;
import me.randomhashtags.randompackage.events.GlobalChallengeParticipateEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.math.BigDecimal;
import java.util.HashMap;

public class IncreaseGlobalChallenge extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.active;
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final String[] values = recipientValues.get(e).split(":");
                final GlobalChallenge c = getGlobalChallenge(values[0]);
                if(c != null) {
                    final ActiveGlobalChallenge a = active.getOrDefault(c, null);
                    if(a != null) {
                        final BigDecimal by = BigDecimal.valueOf(evaluate(values[1]));
                        final GlobalChallengeParticipateEvent ev = new GlobalChallengeParticipateEvent(event, (Player) e, a, by);
                        pluginmanager.callEvent(ev);
                        if(!ev.isCancelled()) {
                            a.increaseValue(e.getUniqueId(), ev.value);
                        }
                    }
                }
            }
        }
    }
}
