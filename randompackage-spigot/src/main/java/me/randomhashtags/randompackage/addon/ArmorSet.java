package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.util.obj.ArmorSetWeaponInfo;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ArmorSet extends Identifiable, Nameable {
    ItemStack getHelmet();
    ItemStack getChestplate();
    ItemStack getLeggings();
    ItemStack getBoots();
    List<ArmorSetWeaponInfo> getWeapons();
    List<String> getArmorLore();
    List<String> getCrystalPerks();
    List<String> getArmorAttributes();
    List<String> getCrystalAttributes();
    List<String> getActivateMessage();
    List<String> getCrystalAppliedMsg();
}
