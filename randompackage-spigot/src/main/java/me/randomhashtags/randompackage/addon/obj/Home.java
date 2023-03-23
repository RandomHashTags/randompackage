package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public final class Home {
	private Location location;
	private String name;
	private UMaterial icon;
	public Home(String name, Location location, UMaterial icon) {
		this.name = name;
		this.location = location;
		this.icon = icon;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public @NotNull String getName() {
		return name;
	}
	public void setName(@NotNull String name) {
		this.name = name;
	}
	public @NotNull UMaterial getIcon() {
		return icon;
	}
	public void setIcon(@NotNull UMaterial icon) {
		this.icon = icon;
	}
}