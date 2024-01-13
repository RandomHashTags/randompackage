package me.randomhashtags.randompackage.attribute.faction;

import me.randomhashtags.randompackage.attribute.AbstractEventAttribute;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.supported.regional.FactionsUUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class AddMaxFactionWarps extends AbstractEventAttribute {
    // TODO: support a Faction plugin that allows Faction Warps
    @Override
    public void execute(@NotNull PendingEventAttribute pending, @NotNull HashMap<String, String> valueReplacements) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final com.massivecraft.factions.Faction f = FactionsUUID.INSTANCE.getFaction(e.getUniqueId());
                if(f != null) {
                }
            }
        }
    }
}
