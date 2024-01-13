package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class Smite extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending, @NotNull HashMap<String, String> valueReplacements) {
        final HashMap<String, Entity> entities = pending.getEntities();
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            final String v = recipientValues.get(e);
            if(v != null) {
                final String[] values = v.split(":");
                final boolean at = values.length >= 2;
                final String value = replaceValue(entities, at ? values[1] : values[0], valueReplacements);
                final World w = e.getWorld();
                final Location l = at ? string_to_location(replaceValue(entities, values[0], valueReplacements)) : e.getLocation();
                if(l != null) {
                    for(int i = 1; i <= (int) evaluate(value); i++) {
                        w.strikeLightning(l);
                    }
                }
            }
        }
    }
}
