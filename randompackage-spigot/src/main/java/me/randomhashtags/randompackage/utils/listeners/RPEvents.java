package me.randomhashtags.randompackage.utils.listeners;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.utils.RPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class RPEvents extends RandomPackageAPI implements Listener {
    private static RPEvents instance;
    public static final RPEvents getRPEvents() {
        if(instance == null) instance = new RPEvents();
        return instance;
    }

    public boolean isEnabled = false;

    public void enable() {
        if(isEnabled) return;
        isEnabled = true;
        pluginmanager.registerEvents(this, randompackage);

        for(Player p : Bukkit.getOnlinePlayers()) {
            RPPlayer.get(p.getUniqueId()).load();
        }
    }
    public void disable() {
        if(!isEnabled) return;
        for(RPPlayer p : new ArrayList<>(RPPlayer.players.values())) {
            p.unload();
        }
        isEnabled = false;
        HandlerList.unregisterAll(this);
    }

    public void backup() {
        for(RPPlayer p : RPPlayer.players.values()) p.backup();
    }


    @EventHandler
    private void playerJoinEvent(PlayerJoinEvent event) {
        RPPlayer.get(event.getPlayer().getUniqueId()).load();
    }
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        RPPlayer.get(event.getPlayer().getUniqueId()).unload();
    }
}
