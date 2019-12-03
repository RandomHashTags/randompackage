package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import static me.randomhashtags.randompackage.api.CustomEnchants.getCustomEnchants;

public class HasCustomEnchantEquipped extends AbstractEventCondition {
    @Override
    public boolean check(Entity entity, String value) {
        boolean has = false;
        if(entity instanceof LivingEntity) {
            final EntityEquipment e = ((LivingEntity) entity).getEquipment();
            if(e != null) {
                final CustomEnchant c = valueOfCustomEnchant(value);
                if(c != null) {
                    final CustomEnchants enchants = getCustomEnchants();
                    final ItemStack[] items = new ItemStack[] {e.getHelmet(), e.getChestplate(), e.getLeggings(), e.getBoots(), EIGHT ? e.getItemInHand() : e.getItemInMainHand(), EIGHT ? null : e.getItemInOffHand()};
                    for(ItemStack is : items) {
                        if(enchants.getEnchantsOnItem(is).containsKey(c)) {
                            has = true;
                            break;
                        }
                    }
                }
            }
        }
        return has == Boolean.parseBoolean(value);
    }
}
