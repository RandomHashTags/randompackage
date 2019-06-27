package me.randomhashtags.randompackage.utils.abstraction;

import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public abstract class AbstractGlobalChallenge extends Saveable {
    private ItemStack display;
    private Set<UUID> participants;
    public long started;

    public ItemStack getDisplayItem() {
        if(display == null) display = api.d(yml, "item");
        return display.clone();
    }
    public Set<UUID> getParticipants() { return participants; }
    public void setParticipants(Set<UUID> participants) { this.participants = participants; }
    public String getTracks() { return yml.getString("settings.tracks"); }
    public long getDuration() { return yml.getLong("settings.duration"); }
    public String getType() { return yml.getString("settings.type"); }
}
