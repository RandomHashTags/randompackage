package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Location;

public class Home {
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public UMaterial getIcon() {
		return icon;
	}
	public void setIcon(UMaterial icon) {
		this.icon = icon;
	}
}