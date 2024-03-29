package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.addon.GlobalChallenge;
import me.randomhashtags.randompackage.addon.living.ActiveGlobalChallenge;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.event.GlobalChallengeParticipateEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;

public final class IncreaseGlobalChallenge extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending, @NotNull HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getEntities();
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        final HashMap<GlobalChallenge, ActiveGlobalChallenge> active = ActiveGlobalChallenge.ACTIVE;
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final String[] values = recipientValues.get(e).split(":");
                final GlobalChallenge c = getGlobalChallenge(values[0]);
                if(c != null) {
                    final ActiveGlobalChallenge a = active.getOrDefault(c, null);
                    if(a != null) {
                        final BigDecimal by = BigDecimal.valueOf(evaluate(replaceValue(entities, values[1], valueReplacements)));
                        final GlobalChallengeParticipateEvent ev = new GlobalChallengeParticipateEvent(event, (Player) e, a, by);
                        PLUGIN_MANAGER.callEvent(ev);
                        if(!ev.isCancelled()) {
                            a.increaseValue(e.getUniqueId(), ev.value);
                        }
                    }
                }
            }
        }
    }
}
