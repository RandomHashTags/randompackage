package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class GiveItem extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        final RandomPackageAPI api = RandomPackageAPI.INSTANCE;
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final ItemStack is = api.createItemStack(null, recipientValues.get(e));
                if(is != null && !is.getType().equals(Material.AIR)) {
                    giveItem((Player) e, is);
                }
            }
        }
    }
}
