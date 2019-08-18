package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Attributable;
import me.randomhashtags.randompackage.addons.utils.Identifiable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ArmorSet extends Attributable, Identifiable {
    ItemStack getHelmet();
    ItemStack getChestplate();
    ItemStack getLeggings();
    ItemStack getBoots();
    List<String> getArmorLore();
    List<String> getWeaponLore();
    List<String> getActivateMessage();
}
