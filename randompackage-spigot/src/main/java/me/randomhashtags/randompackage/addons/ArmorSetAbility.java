package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifiable;
import org.bukkit.event.Event;

public interface ArmorSetAbility extends Identifiable {
    Class getEventClass();
    void onEvent(Event event);
}
