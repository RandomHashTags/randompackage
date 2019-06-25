package me.randomhashtags.randompackage.api.events.servercrates;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.servercrate.ServerCrate;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public class ServerCrateCloseEvent extends RandomPackageEvent {
    public final Player player;
    public final ServerCrate crate;
    public final List<ItemStack> rewards;
    public ServerCrateCloseEvent(Player player, ServerCrate crate, List<ItemStack> rewards) {
        this.player = player;
        this.crate = crate;
        this.rewards = rewards;
    }
}
