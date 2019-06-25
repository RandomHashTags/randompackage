package me.randomhashtags.randompackage.api.events.jackpot;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.living.player.Player;

public class JackpotPurchaseTicketsEvent extends RandomPackageEvent {
    public final Player player;
    public final int amount;
    public final double price;
    public JackpotPurchaseTicketsEvent(Player player, int amount, double price) {
        this.player = player;
        this.amount = amount;
        this.price = price;
    }
}
