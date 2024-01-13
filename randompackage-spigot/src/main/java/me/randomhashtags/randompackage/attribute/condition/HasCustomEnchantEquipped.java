package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class HasCustomEnchantEquipped extends AbstractEventCondition {
    @Override
    public boolean check(@NotNull Entity entity, @NotNull String value) {
        boolean has = false;
        if(entity instanceof LivingEntity) {
            final EntityEquipment equipment = ((LivingEntity) entity).getEquipment();
            if(equipment != null) {
                final CustomEnchantSpigot enchant = valueOfCustomEnchant(value);
                if(enchant != null) {
                    final CustomEnchants enchants = CustomEnchants.INSTANCE;
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
