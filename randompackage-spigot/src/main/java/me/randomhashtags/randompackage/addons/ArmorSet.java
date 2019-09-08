package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifiable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ArmorSet extends Identifiable {
    ItemStack getHelmet();
    ItemStack getChestplate();
    ItemStack getLeggings();
    ItemStack getBoots();
    ItemStack getWeapon();
    List<String> getArmorLore();
    List<String> getWeaponLore();
    List<String> getArmorAttributes();
    List<String> getWeaponAttributes();
    String getCrystalName();
    List<String> getCrystalPerks();
    List<String> getCrystalAttributes();
    List<String> getActivateMessage();
}
