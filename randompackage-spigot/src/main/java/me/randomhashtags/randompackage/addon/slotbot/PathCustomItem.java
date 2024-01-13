package me.randomhashtags.randompackage.addon.slotbot;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class PathCustomItem implements CustomItem {
    private final String path;
    private final ItemStack item;
    private final List<String> commands;

    public PathCustomItem(String path, ItemStack item, List<String> commands) {
        this.path = path;
        this.item = item;
        this.commands = commands;
    }

    @Override
    public @NotNull String getIdentifier() {
        return path;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return item;
    }

    @Override
    public @Nullable List<String> getCommands() {
        return commands;
    }
}
