package me.randomhashtags.randompackage.utils.classes.custombosses;

import me.randomhashtags.randompackage.utils.NamespacedKey;
import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomBoss;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class CustomBoss extends AbstractCustomBoss {

	private ItemStack spawnitem;
	private HashMap<Integer, List<String>> messages;
	private List<CustomBossAttack> attacks;
	private CustomMinion minion;

	public CustomBoss(File f) {
		load(f);
		created(new NamespacedKey(getPlugin, getYamlName()));
	}

	public String getType() { return yml.getString("type").toUpperCase(); }
	public String getName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("name")); }
	public String getScoreboardTitle() { return ChatColor.translateAlternateColorCodes('&', yml.getString("scoreboard.title")); }
	public DisplaySlot getScoreboardSlot() { return DisplaySlot.valueOf(yml.getString("scoreboard.display slot").toUpperCase()); }
	public List<String> getScoreboardScores() { return yml.getStringList("scoreboard.scores"); }
	public ItemStack getSpawnItem() {
		if(spawnitem == null) spawnitem = api.d(yml, "spawn item");
		return spawnitem.clone();
	}
	public List<String> getAttributes() { return yml.getStringList("attributes"); }
	public List<String> getRewards() { return yml.getStringList("rewards"); }
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
	public int getMessageRadius() { return yml.getInt("messages.radius"); }
	public int getMaxMinions() { return yml.getInt("minion.max"); }
	public CustomMinion getMinion() {
		if(minion == null) minion = new CustomMinion(yml.getString("minion.type").toUpperCase(), ChatColor.translateAlternateColorCodes('&', yml.getString("minion.name")), yml.getStringList("minion.attributes"));
		return minion;
	}
}
