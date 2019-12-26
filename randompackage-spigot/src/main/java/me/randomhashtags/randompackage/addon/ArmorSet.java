package me.randomhashtags.randompackage.addon;

import com.sun.istack.internal.NotNull;
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
    default ArmorSetWeaponInfo getWeapon(@NotNull String identifier) {
        final List<ArmorSetWeaponInfo> weapons = getWeapons();
        if(weapons != null) {
            for(ArmorSetWeaponInfo weapon : weapons) {
                if(weapon.getIdentifier().equals(identifier)) {
                    return weapon;
                }
            }
        }
        return null;
    }
    List<String> getArmorLore();
    List<String> getCrystalPerks();
    List<String> getArmorAttributes();
    List<String> getCrystalAttributes();
    List<String> getActivateMessage();
    List<String> getCrystalAppliedMsg();
}
