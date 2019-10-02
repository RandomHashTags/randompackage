package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class RemoveItem extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final String value = replaceValue(recipientValues.get(e), valueReplacements);
                final ItemStack g = givedpitem.valueOf(value);
                if(g != null) {
                    removeItem((Player) e, g, g.getAmount());
                }
            }
        }
    }
}
