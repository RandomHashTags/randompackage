package me.randomhashtags.randompackage.attribute.faction;

import me.randomhashtags.randompackage.attribute.AbstractEventAttribute;
import me.randomhashtags.randompackage.attributesys.EventEntities;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.supported.regional.FactionsUUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class AddFactionPower extends AbstractEventAttribute implements EventEntities {
    @Override
    public void execute(@NotNull PendingEventAttribute pending, @NotNull HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        final FactionsUUID factions = FactionsUUID.INSTANCE;
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final com.massivecraft.factions.Faction f = factions.getFaction(e.getUniqueId());
                if(f != null) {
                    f.setPowerBoost(f.getPowerBoost()+evaluate(replaceValue(getEntities(event), recipientValues.get(e), valueReplacements)));
                }
            }
        }
    }
}
