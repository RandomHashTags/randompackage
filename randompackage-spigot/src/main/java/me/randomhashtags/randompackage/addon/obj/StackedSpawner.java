package me.randomhashtags.randompackage.addon.obj;

import org.bukkit.entity.EntityType;

public class StackedSpawner {
    private EntityType type;
    private int stack;
    public StackedSpawner(EntityType type, int stack) {
        this.type = type;
        this.stack = stack;
    }
    public EntityType getType() { return type; }
    public int getStack() { return stack; }
    public void setStack(int stack) { this.stack = stack; }
}
