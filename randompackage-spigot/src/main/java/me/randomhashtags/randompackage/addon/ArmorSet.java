package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.api.CustomArmor;
import me.randomhashtags.randompackage.util.obj.ArmorSetWeaponInfo;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface ArmorSet extends Identifiable, Nameable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "customarmor", "customarmorcrystal", "multicustomarmorcrystal" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final String[] values = originalInput.split(":");
        final ArmorSet set = getArmorSet(values[1]);
        if(set != null) {
            switch (lowercaseInput.split(":")[0]) {
                case "customarmor":
                    String type = values.length == 2 ? "random" : values[2];
                    final int slot = RANDOM.nextInt(4);
                    type = type.equals("random") ? slot == 0 ? "helmet" : slot == 1 ? "chestplate" : slot == 2 ? "leggings" : "boots" : type;
                    final ArmorSetWeaponInfo weapon = set.getWeapon(type);
                    final ItemStack item = type.equals("helmet") ? set.getHelmet() : type.equals("chestplate") ? set.getChestplate() : type.equals("leggings") ? set.getLeggings() : type.equals("boots") ? set.getBoots() : weapon != null ? weapon.getItem() : null;
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
                    return CustomArmor.INSTANCE.getCrystal(set, percent);
                case "multicustomarmorcrystal":
                    return null;
                default:
                    return null;
            }
        }
        return null;
    }

    @NotNull ItemStack getHelmet();
    @NotNull ItemStack getChestplate();
    @NotNull ItemStack getLeggings();
    @NotNull ItemStack getBoots();
    @NotNull List<ArmorSetWeaponInfo> getWeapons();
    @Nullable
    default ArmorSetWeaponInfo getWeapon(@NotNull String identifier) {
        final List<ArmorSetWeaponInfo> weapons = getWeapons();
        for(ArmorSetWeaponInfo weapon : weapons) {
            if(weapon.getIdentifier().equals(identifier)) {
                return weapon;
            }
        }
        return null;
    }
    @NotNull List<String> getArmorLore();
    @NotNull List<String> getCrystalPerks();
    @NotNull List<String> getArmorAttributes();
    @NotNull List<String> getCrystalAttributes();
    @NotNull List<String> getActivateMessage();
    @NotNull List<String> getCrystalAppliedMsg();
}
