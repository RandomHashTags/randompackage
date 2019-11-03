package me.randomhashtags.randompackage.attribute;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;

public class Teleport extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<String, Entity> entities, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {
        for(Entity e : recipientValues.keySet()) {
            final String value = replaceValue(entities, recipientValues.get(e), valueReplacements);
            final Location l = toLocation(value);
            if(l != null) {
                e.teleport(l, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
    }
}
