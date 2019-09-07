package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class PlayerExpGainEvent extends PlayerExpChangeEvent {
    public PlayerExpGainEvent(Player player, int amount) {
        super(player, amount);
    }
}
