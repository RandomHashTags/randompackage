package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifiable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ArmorSet extends Identifiable {
    String getName();
    ItemStack getHelmet();
    ItemStack getChestplate();
    ItemStack getLeggings();
    ItemStack getBoots();
    ItemStack getWeapon();
    List<String> getArmorLore();
    List<String> getWeaponLore();
    List<String> getCrystalPerks();
    List<String> getArmorAttributes();
    List<String> getWeaponAttributes();
    List<String> getCrystalAttributes();
    List<String> getActivateMessage();
}
