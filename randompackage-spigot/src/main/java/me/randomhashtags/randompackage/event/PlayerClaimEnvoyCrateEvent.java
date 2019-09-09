package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.living.LivingEnvoyCrate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerClaimEnvoyCrateEvent extends RPEventCancellable {
	public final Location location;
	public final LivingEnvoyCrate type;
	public PlayerClaimEnvoyCrateEvent(Player player, Location location, LivingEnvoyCrate type) {
		super(player);
		this.location = location;
		this.type = type;
	}
}
