package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.universal.UVersion;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import java.util.*;

public class CustomEnchantEntity {
	public static HashMap<String, CustomEnchantEntity> paths;
	private static UVersion uv;
	private EntityType type;
	private String path, customname;
	private List<String> attributes;
	private boolean canTargetSummoner, dropsItemsUponDeath;

	public CustomEnchantEntity(EntityType type, String path, String customname, List<String> attributes, boolean canTargetSummoner, boolean dropsItemsUponDeath) {
		if(paths == null) {
			paths = new HashMap<>();
			uv = UVersion.getUVersion();
		}
		this.type = type;
		this.path = path;
		this.customname = ChatColor.translateAlternateColorCodes('&', customname);
		this.attributes = attributes;
		this.canTargetSummoner = canTargetSummoner;
		this.dropsItemsUponDeath = dropsItemsUponDeath;
		paths.put(path, this);
	}
	public EntityType getEntityType() {
		return type;
	}
	public String getPath() {
		return path;
	}
	public String getCustomName() {
		return customname;
	}
	public List<String> getAttributes() {
		return attributes;
	}
	public boolean canTargetSummoner() {
		return canTargetSummoner; }
	public boolean dropsItemsUponDeath() {
		return dropsItemsUponDeath;
	}
	public void spawn(LivingEntity summoner, LivingEntity target, Event event) {
		final LivingEntity le = uv.getEntity(type.name(), summoner.getLocation(), true);
		new LivingCustomEnchantEntity(this, event, summoner, le, target);
	}

	public static void deleteAll() {
		paths = null;
		uv = null;
	}
}