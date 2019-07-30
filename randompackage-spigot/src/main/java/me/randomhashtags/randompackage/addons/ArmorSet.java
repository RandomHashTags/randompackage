package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifyable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ArmorSet extends Identifyable {
    ItemStack getHelmet();
    ItemStack getChestplate();
    ItemStack getLeggings();
    ItemStack getBoots();
    List<String> getArmorLore();
    List<String> getWeaponLore();
    List<String> getAttributes();
    List<String> getActivateMessage();
}
