package me.randomhashtags.randompackage.supported.mechanics;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.attribute.mcmmo.SetGainedXp;
import me.randomhashtags.randompackage.util.Reflect;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public final class MCMMOAPI extends Reflect {
	private static MCMMOAPI instance;
	public static MCMMOAPI getMCMMOAPI() {
		if(instance == null) instance = new MCMMOAPI();
		return instance;
	}

	private boolean isClassic = false;
	protected static YamlConfiguration itemsConfig;
	public ItemStack creditVoucher, levelVoucher, xpVoucher;

	public String getIdentifier() { return "MECHANIC_MCMMO"; }
	public void load() {
		final long started = System.currentTimeMillis();
		new SetGainedXp().load();
		itemsConfig = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "items.yml"));
		creditVoucher = givedpitem.items.get("mcmmocreditvoucher");
		levelVoucher = givedpitem.items.get("mcmmolevelvoucher");
		xpVoucher = givedpitem.items.get("mcmmoxpvoucher");

		final String version = PLUGIN_MANAGER.getPlugin("mcMMO").getDescription().getVersion();
		isClassic = version.startsWith("1.");
		sendConsoleMessage("&6[RandomPackage] &aHooked MCMMO " + (isClassic ? "Classic" : "Overhaul") + " (" + version + ") &e(took " + (System.currentTimeMillis()-started) + "ms)");
	}
	public void unload() {
		itemsConfig = null;
	}

	public boolean isClassic() { return isClassic; }
	public String getSkillName(String input, String o) {
		if(isClassic) {
			for(com.gmail.nossr50.datatypes.skills.SkillType type : com.gmail.nossr50.datatypes.skills.SkillType.values()) {
				final String n = type.name();
				if(input.equals(o.replace("{SKILL}", getSkillName(n)))) {
					return n;
				}
			}
		} else {
			for(com.gmail.nossr50.datatypes.skills.PrimarySkillType type : com.gmail.nossr50.datatypes.skills.PrimarySkillType.values()) {
				final String n = type.name();
				if(input.equals(o.replace("{SKILL}", getSkillName(n)))) {
					return n;
				}
			}
		}
		return null;
	}
	public String getSkillName(String skill) {
		if(skill.equalsIgnoreCase("random")) {
			skill = getRandomSkill();
		}
		final String a = itemsConfig.getString("mcmmo vouchers.skill names." + skill.toLowerCase().replace("_skills", ""));
		return a != null ? colorize(a) : null;
	}
	public String getRandomSkill() {
		if(isClassic) {
			final com.gmail.nossr50.datatypes.skills.SkillType[] a = com.gmail.nossr50.datatypes.skills.SkillType.values();
			return a[RANDOM.nextInt(a.length)].name();
		} else {
			final com.gmail.nossr50.datatypes.skills.PrimarySkillType[] a = com.gmail.nossr50.datatypes.skills.PrimarySkillType.values();
			return a[RANDOM.nextInt(a.length)].name();
		}
	}
	public String getSkillName(McMMOPlayerXpGainEvent event) {
		try {
			final String skill;
			final Field f = getPrivateField(event.getClass(), "skill", true);
			f.setAccessible(true);
			if(isClassic) {
				skill = ((com.gmail.nossr50.datatypes.skills.SkillType) f.get(event)).name().toLowerCase();
			} else {
				skill = ((com.gmail.nossr50.datatypes.skills.PrimarySkillType) f.get(event)).name().toLowerCase();
			}
			f.setAccessible(false);
			return skill;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void addRawXP(Player player, String skill, int xp) {
		if(skill != null) {
			ExperienceAPI.addRawXP(player, skill, xp);
		}
	}
	public void addLevels(Player player, String skill, int levels) {
		if(skill != null) {
			ExperienceAPI.addLevel(player, skill, levels);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void mcmmoPlayerXpGainEvent(McMMOPlayerXpGainEvent event) {
		final Player player = event.getPlayer();
		final String skill = getSkillName(event);
		if(skill != null) {
			final CustomEnchants e = CustomEnchants.getCustomEnchants();
			e.triggerEnchants(event, e.getEnchants(player));
		}
	}
	
	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		final ItemStack it = event.getItem();
		if(it == null || it.getType().equals(Material.AIR) || !it.hasItemMeta() || !it.getItemMeta().hasDisplayName() || !it.getItemMeta().hasLore()) {
			return;
		} else {
			final ItemMeta m = it.getItemMeta();
			final String d = m.getDisplayName();
			final boolean credit = d.equals(creditVoucher.getItemMeta().getDisplayName()), level = d.equals(levelVoucher.getItemMeta().getDisplayName()), xpv = d.equals(xpVoucher.getItemMeta().getDisplayName());
			if(credit || level || xpv) {
				final Player player = event.getPlayer();
				int numberslot = -1, skillslot = -1;
				final String itemtype = credit ? "credit" : level ? "level" : "xp";
				final List<String> voucherLore = getStringList(itemsConfig, "mcmmo vouchers." + itemtype + ".lore"), msg = getStringList(itemsConfig, "mcmmo vouchers.messages.redeem " + itemtype);
				for(int i = 0; i < voucherLore.size(); i++) {
					final String target = voucherLore.get(i);
					if(target.contains("{AMOUNT}")) numberslot = i;
					if(target.contains("{SKILL}")) skillslot = i;
				}
				if(numberslot == -1 || skillslot == -1) return;
				final List<String> lore = m.getLore();
				final String input = ChatColor.stripColor(lore.get(numberslot)), o = colorize(voucherLore.get(skillslot));
				final String type = getSkillName(lore.get(skillslot), o);
				final int xp = getRemainingInt(input);
				event.setCancelled(true);
				player.updateInventory();

				final HashMap<String, String> replacements = new HashMap<>();
				replacements.put("{XP}", formatInt(xp));
				replacements.put("{AMOUNT}", formatInt(xp));
				replacements.put("{SKILL}", type);
				sendStringListMessage(player, msg, replacements);

				if(xpv) {
					addRawXP(player, type, xp);
				} else if(level) {
					addLevels(player, type, xp);
				} else return;
				removeItem(player, it, 1);
				playSound(itemsConfig, "mcmmo vouchers.sounds.redeem " + itemtype, player, player.getLocation(), false);
			}
		}
	}
}