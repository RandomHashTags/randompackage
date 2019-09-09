package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Location;

public class Home {
	public Location location;
	public String name;
	public UMaterial icon;
	public Home(String name, Location location, UMaterial icon) {
		this.name = name;
		this.location = location;
		this.icon = icon;
	}
}