package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemIsInventoryPet extends AbstractEventCondition implements RPItemStack {
    @Override
    public boolean check(Event event, String value) {
        if(event instanceof PlayerInteractEvent) {
            final PlayerInteractEvent e = (PlayerInteractEvent) event;
            final String info = getRPItemStackValue(e.getItem(), "InventoryPetInfo");
            return info != null && info.split(":")[0].equals(value);
        }
        return false;
    }
}
