package me.randomhashtags.randompackage.util.obj;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ArmorSetWeaponInfo extends TObject {
    private List<String> attributes;
    public ArmorSetWeaponInfo(String identifier, ItemStack item, List<String> setlore, List<String> attributes) {
        super(identifier, item, setlore);
        this.attributes = attributes;
    }
    public final String getIdentifier() { return (String) first; }
    public final ItemStack getItem() { return (ItemStack) second; }
    public final List<String> getSetLore() { return (List<String>) third; }
    public final List<String> getAttributes() { return attributes; }
}
