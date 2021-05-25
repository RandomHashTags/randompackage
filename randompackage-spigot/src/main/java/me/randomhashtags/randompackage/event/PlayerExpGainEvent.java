package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;

public final class PlayerExpGainEvent extends RPEventCancellable {
    private int amount;

    public PlayerExpGainEvent(Player player, int amount) {
        super(player);
        this.amount = amount;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
