package me.randomhashtags.randompackage.attribute.faction;

import com.massivecraft.factions.Faction;
import me.randomhashtags.randompackage.attribute.AbstractEventAttribute;
import me.randomhashtags.randompackage.attributesys.EventEntities;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashMap;

public class AddFactionPower extends AbstractEventAttribute implements EventEntities {
    @Override
    public void execute(Event event, HashMap<String, Entity> entities, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final Faction f = factions.getFaction(e.getUniqueId());
                if(f != null) {
                    f.setPowerBoost(f.getPowerBoost()+evaluate(replaceValue(getEntities(event), recipientValues.get(e), valueReplacements)));
                }
            }
        }
    }
}
