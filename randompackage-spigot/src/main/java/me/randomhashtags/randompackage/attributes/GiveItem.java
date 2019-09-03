package me.randomhashtags.randompackage.attributes;

import me.randomhashtags.randompackage.utils.addons.AbstractEventAttribute;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class GiveItem extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final ItemStack is = api.d(null, recipientValues.get(e));
                if(is != null && !is.getType().equals(Material.AIR)) {
                    giveItem((Player) e, is);
                }
            }
        }
    }
}
