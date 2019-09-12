package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import static me.randomhashtags.randompackage.api.CustomEnchants.getCustomEnchants;

public class HasCustomEnchantEquipped extends AbstractEventCondition {
    @Override
    public boolean check(Entity entity, String value) {
        if(entity instanceof LivingEntity) {
            final EntityEquipment e = ((LivingEntity) entity).getEquipment();
            if(e != null) {
                final CustomEnchant c = valueOfCustomEnchant(value);
                if(c != null) {
                    final boolean eight = version.contains("1.8");
                    final ItemStack[] items = new ItemStack[] {e.getHelmet(), e.getChestplate(), e.getLeggings(), e.getBoots(), eight ? e.getItemInHand() : e.getItemInMainHand(), eight ? null : e.getItemInOffHand()};
                    for(ItemStack is : items) {
                        if(getCustomEnchants().getEnchants(is).containsKey(c)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
