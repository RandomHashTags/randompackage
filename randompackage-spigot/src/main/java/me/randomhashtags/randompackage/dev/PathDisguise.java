package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.dev.Disguise;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import me.randomhashtags.randompackage.enums.Feature;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PathDisguise extends RPAddonSpigot implements Disguise {
    private final String identifier, name;
    private final int slot;

    public PathDisguise(String identifier, int slot, String name) {
        super(null);
        this.identifier = identifier.toUpperCase();
        this.slot = slot;
        this.name = name;
        register(Feature.DISGUISE, this);
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }
    @Override
    public String getEntityType() {
        return identifier;
    }

    public int getSlot() {
        return slot;
    }
    public @NotNull String getName() {
        return name;
    }
    public boolean allowsBaby() {
        return Disguises.INSTANCE.config.getBoolean("entity types." + identifier + ".baby");
    }
}
