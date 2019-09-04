package me.randomhashtags.randompackage.attributes.conditions;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.attributes.AbstractEventCondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class HasCustomEnchantEquipped extends AbstractEventCondition {
    // TODO: finish this condition
    public boolean check(Entity entity, String value) {
        if(entity instanceof LivingEntity) {
            final EntityEquipment e = ((LivingEntity) entity).getEquipment();
            if(e != null) {
                final CustomEnchant c = valueOfCustomEnchant(value);
                if(c != null) {
                    final boolean eight = version.contains("1.8");
                    final ItemStack[] items = new ItemStack[] {e.getHelmet(), e.getChestplate(), e.getLeggings(), e.getBoots(), eight ? e.getItemInHand() : e.getItemInMainHand(), eight ? null : e.getItemInOffHand()};
                    for(ItemStack is : items) {
                        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                        }
                    }
                }
            }
        }
        return false;
    }
}
