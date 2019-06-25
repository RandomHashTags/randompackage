package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.ArrayList;

public class RPEvents extends RandomPackageAPI {

    private static RPEvents instance;
    public static final RPEvents getRPEvents() {
        if(instance == null) instance = new RPEvents();
        return instance;
    }

    public boolean isEnabled = false;

    public void enable() {
        if(isEnabled) return;
        isEnabled = true;
        eventmanager.registerListeners(randompackage, this);

        for(Player p : Sponge.getServer().getOnlinePlayers()) {
            RPPlayer.get(p.getUniqueId()).load();
        }
    }
    public void disable() {
        if(!isEnabled) return;
        for(RPPlayer p : new ArrayList<>(RPPlayer.players.values())) {
            p.unload();
        }
        isEnabled = false;
        eventmanager.unregisterListeners(this);
    }

    public void backup() {
        for(RPPlayer p : RPPlayer.players.values()) p.backup();
    }

    @Listener
    private void playerJoinEvent(ClientConnectionEvent.Join event) {
        RPPlayer.get(event.getTargetEntity().getUniqueId()).load();
    }
    @Listener
    private void playerQuitEvent(ClientConnectionEvent.Disconnect event) {
        RPPlayer.get(event.getTargetEntity().getUniqueId()).unload();
    }
}
