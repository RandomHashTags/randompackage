package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addons.ArmorSet;
import me.randomhashtags.randompackage.events.customenchant.PvAnyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class newCustomArmor extends EventAttributes implements Listener {
    private static newCustomArmor instance;
    public static newCustomArmor getCustomArmor() {
        if(instance == null) instance = new newCustomArmor();
        return instance;
    }

    public String getIdentifier() { return "ARMOR_SETS"; }
    public void load() {
    }
    public void unload() {
    }

    @EventHandler
    private void pvanyEvent(PvAnyEvent event) {
        final ArmorSet a = valueOfArmorSet(event.damager);
        if(a != null) {
            trigger(event, a.getAttributes());
        }
    }
}
