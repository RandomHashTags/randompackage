package me.randomhashtags.randompackage.utils.universal;

import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class UInventory {
    private Inventory inventory;
    private InventoryArchetype type;
    private Container holder;
    private int size;
    private String title;
    public UInventory(Container holder, int size, String title) {
        this.holder = holder;
        this.size = size;
        this.title = title;
        type = InventoryArchetypes.CHEST;
    }
    public UInventory(Container holder, InventoryArchetype type, String title) {
        this.holder = holder;
        this.type = type;
        this.size = 0;
        this.title = title;
    }
    public Inventory getInventory() {
        if(inventory == null) inventory = Inventory.builder().of(type).property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(title))).build(getPlugin);
        return inventory;
    }
    public Container getHolder() { return holder; }
    public InventoryArchetype getType() { return type; }
    public int getSize() { return size; }
    public String getTitle() { return title; }
}
