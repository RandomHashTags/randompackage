package me.randomhashtags.randompackage.utils.classes.globalchallenges;

import me.randomhashtags.randompackage.api.GlobalChallenges;
import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class GlobalChallenge extends UVersion {
	public static TreeMap<String, GlobalChallenge> challenges;
	private static GlobalChallenges globalchallenges;

	private File f;
	private YamlConfiguration yml;
	private ItemStack display;
	public List<UUID> participants;
	public long started;

	public GlobalChallenge(File f, List<UUID> participants) {
		if(globalchallenges == null) {
			globalchallenges = GlobalChallenges.getChallenges();
			challenges = new TreeMap<>();
		}
		this.f = f;
		yml = YamlConfiguration.loadConfiguration(f);
		this.participants = participants;
		challenges.put(getYamlName(), this);
	}
	public File getFile() { return f; }
	public YamlConfiguration getYaml() { return yml; }
	public String getYamlName() { return f.getName().split("\\.yml")[0]; }
	public String getTracks() { return yml.getString("settings.tracks"); }
	public long getDuration() { return yml.getLong("settings.duration"); }
	public String getType() { return yml.getString("settings.type"); }
	public ItemStack getDisplayItem() {
		if(display == null) display = globalchallenges.d(yml, "item");
		return display.clone();
	}

	public ActiveGlobalChallenge start() {
		return start(System.currentTimeMillis(), new HashMap<>());
	}
	public ActiveGlobalChallenge start(long started, HashMap<UUID, Double> participants) {
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> a = ActiveGlobalChallenge.active;
		return a != null ? a.getOrDefault(this, new ActiveGlobalChallenge(started, this, participants)) : new ActiveGlobalChallenge(started, this, participants);
	}
	public boolean isActive() {
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> a = ActiveGlobalChallenge.active;
		return a != null && a.containsKey(this);
	}

	public static void deleteAll() {
		challenges = null;
		globalchallenges = null;
	}
}
