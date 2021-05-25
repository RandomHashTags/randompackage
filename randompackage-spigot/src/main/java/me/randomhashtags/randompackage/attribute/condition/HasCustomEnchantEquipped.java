package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import static me.randomhashtags.randompackage.api.CustomEnchants.getCustomEnchants;

public final class HasCustomEnchantEquipped extends AbstractEventCondition {
    @Override
    public boolean check(Entity entity, String value) {
        boolean has = false;
        if(entity instanceof LivingEntity) {
            final EntityEquipment equipment = ((LivingEntity) entity).getEquipment();
            if(equipment != null) {
                final CustomEnchant enchant = valueOfCustomEnchant(value);
                if(enchant != null) {
                    final CustomEnchants enchants = getCustomEnchants();
                    final ItemStack[] items = new ItemStack[] {equipment.getHelmet(), equipment.getChestplate(), equipment.getLeggings(), equipment.getBoots(), EIGHT ? equipment.getItemInHand() : equipment.getItemInMainHand(), EIGHT ? null : equipment.getItemInOffHand()};
                    for(ItemStack is : items) {
                        if(enchants.getEnchantsOnItem(is).containsKey(enchant)) {
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
