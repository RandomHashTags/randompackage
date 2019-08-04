package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifiable;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public interface ArmorSet extends Identifiable {
    ItemStack getHelmet();
    ItemStack getChestplate();
    ItemStack getLeggings();
    ItemStack getBoots();
    List<String> getArmorLore();
    List<String> getWeaponLore();
    List<String> getAttributes();
    List<String> getActivateMessage();
}
