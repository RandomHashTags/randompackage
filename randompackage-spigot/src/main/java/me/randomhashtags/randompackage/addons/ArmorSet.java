package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.utils.RPAddon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public abstract class ArmorSet extends RPAddon {
    public abstract ItemStack getHelmet();
    public abstract ItemStack getChestplate();
    public abstract ItemStack getLeggings();
    public abstract ItemStack getBoots();
    public abstract List<String> getArmorLore();
    public abstract List<String> getWeaponLore();
    public abstract List<String> getAttributes();
    public abstract List<String> getActivateMessage();

    public static ArmorSet valueOf(Player player) {
        if(armorsets != null && player != null) {
            final PlayerInventory pi = player.getInventory();
            final ItemStack h = pi.getHelmet(), c = pi.getChestplate(), l = pi.getLeggings(), b = pi.getBoots();
            for(ArmorSet set : armorsets.values()) {
                final List<String> a = set.getArmorLore();
                if(a != null &&
                        (h != null && h.hasItemMeta() && h.getItemMeta().hasLore() && h.getItemMeta().getLore().containsAll(a)
                                && c != null && c.hasItemMeta() && c.getItemMeta().hasLore() && c.getItemMeta().getLore().containsAll(a)
                                && l != null && l.hasItemMeta() && l.getItemMeta().hasLore() && l.getItemMeta().getLore().containsAll(a)
                                && b != null && b.hasItemMeta() && b.getItemMeta().hasLore() && b.getItemMeta().getLore().containsAll(a))) {
                    return set;
                }
            }
        }
        return null;
    }
}
