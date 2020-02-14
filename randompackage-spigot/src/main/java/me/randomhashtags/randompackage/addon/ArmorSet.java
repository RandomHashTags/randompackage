package me.randomhashtags.randompackage.addon;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.api.CustomArmor;
import me.randomhashtags.randompackage.util.obj.ArmorSetWeaponInfo;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public interface ArmorSet extends Identifiable, Nameable, GivedpItemable {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "customarmor", "customarmorcrystal", "multicustomarmorcrystal" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final String[] values = originalInput.split(":");
        final ArmorSet set = getArmorSet(values[1]);
        if(set != null) {
            switch (lowercaseInput.split(":")[0]) {
                case "customarmor":
                    String type = values.length == 2 ? "random" : values[2];
                    final int R = RANDOM.nextInt(4);
                    type = type.equals("random") ? R == 0 ? "helmet" : R == 1 ? "chestplate" : R == 2 ? "leggings" : R == 3 ? "boots" : null : type;
                    final ArmorSetWeaponInfo weapon = set.getWeapon(type);
                    final ItemStack item = type != null ? type.equals("helmet") ? set.getHelmet() : type.equals("chestplate") ? set.getChestplate() : type.equals("leggings") ? set.getLeggings() : type.equals("boots") ? set.getBoots() : weapon != null ? weapon.getItem() : null : null;
                    if(item != null) {
                        final ItemMeta itemMeta = item.getItemMeta();
                        final List<String> lore = new ArrayList<>();
                        if(itemMeta.hasLore()) {
                            lore.addAll(itemMeta.getLore());
                        }
                        lore.addAll(set.getArmorLore());
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                    }
                    return item;
                case "customarmorcrystal":
                    final int percent = values.length >= 3 && !values[2].equals("random") ? Integer.parseInt(values[2]) : RANDOM.nextInt(101);
                    return CustomArmor.getCustomArmor().getCrystal(set, percent);
                case "multicustomarmorcrystal":
                    return AIR;
                default:
                    return AIR;
            }
        }
        return AIR;
    }

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
