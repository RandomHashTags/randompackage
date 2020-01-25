package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static me.randomhashtags.randompackage.RandomPackageAPI.API;

public class GiveItem extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final ItemStack is = API.d(null, recipientValues.get(e));
                if(is != null && !is.getType().equals(Material.AIR)) {
                    giveItem((Player) e, is);
                }
            }
        }
    }
}
