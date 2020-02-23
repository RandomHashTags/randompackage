package me.randomhashtags.randompackage.util.obj;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class ArmorSetWeaponInfo extends TObject {
    private List<String> attributes;
    public ArmorSetWeaponInfo(String identifier, ItemStack item, List<String> setlore, List<String> attributes) {
        super(identifier, item, setlore);
        this.attributes = attributes;
    }
    public String getIdentifier() {
        return (String) first;
    }
    public ItemStack getItem() {
        return (ItemStack) second;
    }
    public List<String> getSetLore() {
        return (List<String>) third;
    }
    public List<String> getAttributes() {
        return attributes;
    }
}
