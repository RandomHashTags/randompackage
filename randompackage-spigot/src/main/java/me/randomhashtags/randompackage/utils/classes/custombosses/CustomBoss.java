package me.randomhashtags.randompackage.utils.classes.custombosses;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomBoss extends UVersion {
	public static HashMap<String, CustomBoss> bosses;
	private static RandomPackageAPI api;

	private YamlConfiguration yml;
	private String ymlName, spawnableRegion, type, name, scoreboardTitle;
	private DisplaySlot scoreboardSlot;
	private ItemStack spawnitem;
	private List<String> attributes, rewards, scores;
	private HashMap<Integer, List<String>> messages;
	private int messageRadius, maxMinions;
	private List<CustomBossAttack> attacks;
	private CustomMinion minion;
	public CustomBoss(File f) {
		if(bosses == null) {
			bosses = new HashMap<>();
			api = RandomPackageAPI.getAPI();
		}
		yml = YamlConfiguration.loadConfiguration(f);
		ymlName = f.getName().split("\\.yml")[0];
		messageRadius = yml.getInt("messages.radius");
		maxMinions = yml.getInt("minion.max");
		bosses.put(ymlName, this);
	}
	public YamlConfiguration getYaml() { return yml; }
	public String getYamlName() { return ymlName; }
	public String getSpawnableRegion() {
		if(spawnableRegion == null) spawnableRegion = yml.getString("spawnable region").toUpperCase();
		return spawnableRegion;
	}
	public String getType() {
		if(type == null) type = yml.getString("type").toUpperCase();
		return type;
	}
	public String getName() {
		if(name == null) name = ChatColor.translateAlternateColorCodes('&', yml.getString("name"));
		return name;
	}
	public String getScoreboardTitle() {
		if(scoreboardTitle == null) scoreboardTitle = ChatColor.translateAlternateColorCodes('&', yml.getString("scoreboard.title"));
		return scoreboardTitle;
	}
	public DisplaySlot getScoreboardSlot() {
		if(scoreboardSlot == null) scoreboardSlot = DisplaySlot.valueOf(yml.getString("scoreboard.display slot").toUpperCase());
		return scoreboardSlot;
	}
	public List<String> getScoreboardScores() {
		if(scores == null) scores = yml.getStringList("scoreboard.scores");
		return scores;
	}
	public ItemStack getSpawnItem() {
		if(spawnitem == null) spawnitem = api.d(yml, "spawn item");
		return spawnitem.clone();
	}
	public List<String> getAttributes() {
		if(attributes == null) attributes = yml.getStringList("attributes");
		return attributes;
	}
	public List<String> getRewards() {
		if(rewards == null) rewards = yml.getStringList("rewards");
		return rewards;
	}
	public List<CustomBossAttack> getAttacks() {
		if(attacks == null) {
			attacks = new ArrayList<>();
			final ConfigurationSection cs = yml.getConfigurationSection("attacks");
			if(cs != null) {
				for(String s : cs.getKeys(false)) {
					attacks.add(new CustomBossAttack(yml.getInt("attacks." + s + ".chance"), yml.getInt("attacks." + s + ".radius"), yml.getStringList("attacks." + s + ".attack")));
				}
			}
		}
		return attacks;
	}
	public HashMap<Integer, List<String>> getMessages() {
		if(messages == null) {
			messages = new HashMap<>();
			messages.put(-5, yml.getStringList("messages.summon"));
			messages.put(-4, yml.getStringList("messages.summon broadcast"));
			messages.put(-3, yml.getStringList("messages.defeated"));
			messages.put(-2, yml.getStringList("messages.defeated broadcast"));
			for(int i = 0; i <= 100; i++) {
				final List<String> s = yml.getStringList("messages." + i);
				if(!s.isEmpty()) {
					messages.put(i, s);
				}
			}
		}
		return messages;
	}
	public int getMessageRadius() { return messageRadius; }
	public int getMaxMinions() { return maxMinions; }
	public CustomMinion getMinion() {
		if(minion == null) {
			minion = new CustomMinion(yml.getString("minion.type").toUpperCase(), ChatColor.translateAlternateColorCodes('&', yml.getString("minion.name")), yml.getStringList("minion.attributes"));
		}
		return minion;
	}

	public LivingCustomBoss spawn(LivingEntity summoner, Location location) {
		return new LivingCustomBoss(summoner, getEntity(getType(), location, true), this);
	}

	public static CustomBoss valueOf(ItemStack spawnitem) {
		if(bosses != null && spawnitem != null && spawnitem.hasItemMeta())
			for(CustomBoss c : bosses.values())
				if(c.getSpawnItem().getItemMeta().equals(spawnitem.getItemMeta()))
					return c;
		return null;
	}
	public static void deleteAll() {
		api = null;
		bosses = null;
	}
}
