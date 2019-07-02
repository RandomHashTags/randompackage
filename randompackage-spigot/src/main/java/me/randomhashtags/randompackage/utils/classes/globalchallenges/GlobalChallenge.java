package me.randomhashtags.randompackage.utils.classes.globalchallenges;

import me.randomhashtags.randompackage.utils.NamespacedKey;
import me.randomhashtags.randompackage.utils.abstraction.AbstractGlobalChallenge;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class GlobalChallenge extends AbstractGlobalChallenge {

	private ItemStack display;
	public long started;
	public GlobalChallenge(File f, Set<UUID> participants) {
		load(f);
		created(new NamespacedKey(getPlugin, getYamlName()));
		setParticipants(participants);
	}

	public ItemStack getDisplayItem() {
		if(display == null) display = api.d(yml, "item");
		return display.clone();
	}
	public String getTracks() { return yml.getString("settings.tracks"); }
	public long getDuration() { return yml.getLong("settings.duration"); }
	public String getType() { return yml.getString("settings.type"); }
}
