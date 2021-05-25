package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;

public final class Teleport extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, HashMap<String, String> valueReplacements) {
        final HashMap<String, Entity> entities = pending.getEntities();
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            final String value = replaceValue(entities, recipientValues.get(e), valueReplacements);
            final Location l = toLocation(value);
            if(l != null) {
                e.teleport(l, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
    }
}
