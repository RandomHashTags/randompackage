package me.randomhashtags.randompackage.universal;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class UInventory {
    private Inventory inventory;
    private InventoryHolder holder;
    private InventoryType type;
    private int size;
    private String title;
    public UInventory(InventoryHolder holder, int size, String title) {
        this.holder = holder;
        type = InventoryType.CHEST;
        this.size = size;
        this.title = title;
    }
    public UInventory(InventoryHolder holder, InventoryType type, String title) {
        this.holder = holder;
        this.type = type;
        this.size = 0;
        this.title = title;
    }
    public Inventory getInventory() {
        if(inventory == null) inventory = type == InventoryType.CHEST ? Bukkit.createInventory(holder, size, title) : Bukkit.createInventory(holder, type, title);
        return inventory;
    }
    public InventoryHolder getHolder() { return holder; }
    public InventoryType getType() { return type; }
    public int getSize() { return size; }
    public String getTitle() { return title; }
}
