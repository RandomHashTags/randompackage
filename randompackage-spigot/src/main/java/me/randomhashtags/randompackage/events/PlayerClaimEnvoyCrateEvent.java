package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.LivingEnvoyCrate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerClaimEnvoyCrateEvent extends AbstractCancellable {
	public final Player player;
	public final Location location;
	public final LivingEnvoyCrate type;
	public PlayerClaimEnvoyCrateEvent(Player player, Location location, LivingEnvoyCrate type) {
		this.player = player;
		this.location = location;
		this.type = type;
	}
}
