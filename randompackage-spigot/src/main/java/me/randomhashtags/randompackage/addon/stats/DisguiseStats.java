package me.randomhashtags.randompackage.addon.stats;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import me.randomhashtags.randompackage.dev.Disguise;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class DisguiseStats {
    private EntityType disguise;
    private HashMap<Disguise, String> settings;
    public DisguiseStats(EntityType disguise, HashMap<Disguise, String> settings) {
        this.disguise = disguise;
        this.settings = settings;
    }
    public EntityType getDisguise() { return disguise; }
    public void setDisguise(@Nullable EntityType disguise) { this.disguise = disguise; }

    public HashMap<Disguise, String> getSettings() {
        return settings;
    }
    private String getSettings(Disguise d) {
        return settings.getOrDefault(d, null);
    }
    public boolean isUnlocked(@NotNull Disguise disguise) {
        return getSettings(disguise) != null;
    }
}
