package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public final class RemoveItem extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, HashMap<String, String> valueReplacements) {
        final HashMap<String, Entity> entities = pending.getEntities();
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        final GivedpItem givedpItem = GivedpItem.INSTANCE;
        for(Entity entity : recipientValues.keySet()) {
            if(entity instanceof Player) {
                final Player player = (Player) entity;
                final String value = replaceValue(entities, recipientValues.get(entity), valueReplacements);
                final String[] values = value.split(":");
                ItemStack is = givedpItem.valueOfRPItem(values[0]);
                if(is == null) {
                    final UMaterial material = UMaterial.match(values[0]);
                    if(material != null) {
                        is = material.getItemStack();
                        if(values.length >= 2) {
                            is.setAmount((int) evaluate(values[1]));
                        }
                    }
                }
                if(is != null) {
                    removeItem(player, is, is.getAmount());
                }
            }
        }
    }
}
