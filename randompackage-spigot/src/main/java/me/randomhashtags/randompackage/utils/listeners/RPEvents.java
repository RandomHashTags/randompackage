package me.randomhashtags.randompackage.utils.listeners;

import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.RPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RPEvents extends RPFeature implements Listener {
    private static RPEvents instance;
    public static RPEvents getRPEvents() {
        if(instance == null) instance = new RPEvents();
        return instance;
    }
    public String getIdentifier() { return "RP_EVENTS"; }
    protected RPFeature getFeature() { return getRPEvents(); }
    public void load() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            RPPlayer.get(p.getUniqueId()).load();
        }
    }
    public void unload() {
    }

    public void backup() {
        for(RPPlayer p : RPPlayer.players.values()) {
            p.backup();
        }
    }

    @EventHandler
    private void playerJoinEvent(PlayerJoinEvent event) {
        RPPlayer.get(event.getPlayer().getUniqueId());
    }
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        RPPlayer.get(event.getPlayer().getUniqueId()).unload();
    }
}
