package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.file.RPAddon;
import me.randomhashtags.randompackage.enums.Feature;

public class PathDisguise extends RPAddon implements Disguise {
    private String identifier, name;
    private int slot;
    public PathDisguise(String identifier, int slot, String name) {
        this.identifier = identifier.toUpperCase();
        this.slot = slot;
        this.name = name;
        register(Feature.DISGUISE, this);
    }
    public String getIdentifier() {
        return identifier;
    }
    public String getEntityType() {
        return identifier;
    }

    public int getSlot() {
        return slot;
    }
    public String getName() {
        return name;
    }
    public boolean allowsBaby() {
        return Disguises.getDisguises().config.getBoolean("entity types." + identifier + ".baby");
    }
}
