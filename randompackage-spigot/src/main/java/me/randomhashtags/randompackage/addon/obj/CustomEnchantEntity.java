package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.universal.UVersionable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;

public class CustomEnchantEntity implements UVersionable {
	public static HashMap<String, CustomEnchantEntity> paths;
	private EntityType type;
	private String path, customname;
	private List<String> attributes;
	private boolean canTargetSummoner, dropsItemsUponDeath;

	public CustomEnchantEntity(EntityType type, String path, String customname, List<String> attributes, boolean canTargetSummoner, boolean dropsItemsUponDeath) {
		if(paths == null) {
			paths = new HashMap<>();
		}
		this.type = type;
		this.path = path;
		this.customname = colorize(customname);
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
		return canTargetSummoner;
	}
	public boolean dropsItemsUponDeath() {
		return dropsItemsUponDeath;
	}
	public void spawn(LivingEntity summoner, LivingEntity target) {
		final LivingEntity le = getEntity(type.name(), summoner.getLocation(), true);
		new LivingCustomEnchantEntity(this, summoner, le, target);
	}

	public static void deleteAll() {
		paths = null;
	}
}