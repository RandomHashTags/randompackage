package me.randomhashtags.randompackage.addons.objects;

import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ServerCrateFlare extends UVersion {
	private ItemStack is;
	public List<String> requestMessage, nearbySpawnMessage;
	public int spawnRadius, spawnInDelay, nearbyRadius;
	public ServerCrateFlare(ItemStack is, List<String> requestMessage, int spawnRadius, int spawnInDelay, int nearbyRadius, List<String> nearbySpawnMessage) {
		this.is = is;
		this.requestMessage = requestMessage;
		this.spawnRadius = spawnRadius;
		this.spawnInDelay = spawnInDelay;
		this.nearbyRadius = nearbyRadius;
		this.nearbySpawnMessage = nearbySpawnMessage;
	}
	
	public ItemStack getItem() { return is.clone(); }
	public Location spawn(Player player, Location requestLocation) {
		if(player != null) {
			final String X = formatInt(requestLocation.getBlockX()), Y = formatInt(requestLocation.getBlockY()), Z = formatInt(requestLocation.getBlockZ());
			for(String s : requestMessage)
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{X}", X).replace("{Y}", Y).replace("{Z}", Z)));
			for(Entity e : player.getNearbyEntities(nearbyRadius, nearbyRadius, nearbyRadius))
				if(e instanceof Player)
					for(String s : nearbySpawnMessage) e.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
		}
		final int x = (random.nextInt(2) == 0 ? 1 : -1)*random.nextInt(spawnRadius), z = (random.nextInt(2) == 0 ? 1 : -1)*random.nextInt(spawnRadius);
		Location loc = new Location(requestLocation.getWorld(), requestLocation.getBlockX()+x, 0, requestLocation.getBlockZ()+z);
		loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getWorld().getHighestBlockYAt(loc), loc.getZ());
		final Location l = loc;
		scheduler.scheduleSyncDelayedTask(randompackage, () -> l.getWorld().getBlockAt(l).setType(Material.CHEST), 20*spawnInDelay);
		return l;
	}
	public void delete() {
		is = null;
		requestMessage = null;
		nearbySpawnMessage = null;
		spawnRadius = 0;
		spawnInDelay = 0;
		nearbyRadius = 0;
	}
}
