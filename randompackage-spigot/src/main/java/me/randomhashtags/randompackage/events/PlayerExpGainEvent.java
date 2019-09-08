package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Player;

public class PlayerExpGainEvent extends AbstractCancellable {
    private Player player;
    private int amount;
    public PlayerExpGainEvent(Player player, int amount) {
        this.player = player;
        this.amount = amount;
    }
    public Player getPlayer() { return player; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
