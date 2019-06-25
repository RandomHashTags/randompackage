package me.randomhashtags.randompackage.utils.supported;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import me.randomhashtags.randompackage.api.GlobalChallenges;
import me.randomhashtags.randompackage.api.events.MCMMOXpGainEvent;
import me.randomhashtags.randompackage.utils.CustomEnchantUtils;
import me.randomhashtags.randompackage.utils.supported.plugins.MCMMOOverhaul;
import me.randomhashtags.randompackage.utils.supported.plugins.MCMMOClassic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MCMMOAPI extends GlobalChallenges implements Listener {

	private static MCMMOAPI instance;
	public static final MCMMOAPI getMCMMOAPI() {
		if(instance == null) instance = new MCMMOAPI();
		return instance;
	}

	public boolean isEnabled = false, isClassic = false, gcIsEnabled = false;
	protected static CustomEnchantUtils customenchants;
	protected static YamlConfiguration itemsConfig;
	public ItemStack creditVoucher, levelVoucher, xpVoucher;

	public void enable() {
		if(isEnabled) return;
		customenchants = CustomEnchantUtils.getCustomEnchantUtils();
		isEnabled = true;
		pluginmanager.registerEvents(this, randompackage);
		itemsConfig = YamlConfiguration.loadConfiguration(new File(rpd, "items.yml"));
		gcIsEnabled = GlobalChallenges.getChallenges().isEnabled;
		creditVoucher = givedpitem.items.get("mcmmocreditvoucher");
		levelVoucher = givedpitem.items.get("mcmmolevelvoucher");
		xpVoucher = givedpitem.items.get("mcmmoxpvoucher");

		isClassic = pluginmanager.getPlugin("mcMMO").getDescription().getVersion().startsWith("1.");
		sendConsoleMessage("&6[RandomPackage] &aHooked MCMMO " + (isClassic ? "Classic" : "Overhaul") + " API");
		if(isClassic) {
			MCMMOClassic.getMCMMOClassic().enable();
		} else {
			MCMMOOverhaul.getMCMMOOverhaul().enable();
		}
	}
	public void disable() {
		if(!isEnabled) return;
		customenchants = null;
		itemsConfig = null;
		creditVoucher = null;
		levelVoucher = null;
		xpVoucher = null;
		isEnabled = false;
		if(isClassic) {
			MCMMOClassic.getMCMMOClassic().disable();
		} else {
			MCMMOOverhaul.getMCMMOOverhaul().disable();
		}
		HandlerList.unregisterAll(this);
	}

	public String getSkillName(String input, String o) {
		if(isClassic) {
			return MCMMOClassic.getMCMMOClassic().valueOf(input, o);
		} else {
			return MCMMOOverhaul.getMCMMOOverhaul().valueOf(input, o);
		}
	}
	public void addRawXP(Player player, String skill, int xp) {
		ExperienceAPI.addRawXP(player, skill, xp);
	}
	public void addLevels(Player player, String skill, int levels) {
		ExperienceAPI.addLevel(player, skill, levels);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void mcmmoPlayerXpGainEvent(MCMMOXpGainEvent event) {
		if(!event.isCancelled()) {
			final Player player = event.player;
			customenchants.procPlayerArmor(event, player);
			customenchants.procPlayerItem(event, player, null);

			if(gcIsEnabled) {
				final UUID p = player.getUniqueId();
				final Object S = event.skill;
				final String skill = (isClassic ? ((com.gmail.nossr50.datatypes.skills.SkillType) S).name() : ((com.gmail.nossr50.datatypes.skills.PrimarySkillType) S).name()).toLowerCase();
				final float xp = event.xp;
				increase(event, "mcmmoxpgained", p, xp);
				increase(event, "mcmmoxpgainedin_" + skill, p, xp);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void mcmmoAbilityActivateEvent(McMMOPlayerAbilityActivateEvent event) {
		if(!event.isCancelled() && gcIsEnabled) {
			final UUID player = event.getPlayer().getUniqueId();
			increase(event, "mcmmoabilityused", player, 1);
			increase(event, "mcmmoabilityused_" + event.getAbility().name(), player, 1);
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
				final List<String> a = itemsConfig.getStringList("mcmmo vouchers." + itemtype + ".lore"), msg = itemsConfig.getStringList("mcmmo vouchers.messages.redeem " + itemtype);
				for(int i = 0; i < a.size(); i++) {
					if(a.get(i).contains("{AMOUNT}")) numberslot = i;
					if(a.get(i).contains("{SKILL}")) skillslot = i;
				}
				if(numberslot == -1 || skillslot == -1) return;
				final List<String> L = m.getLore();
				final String input = L.get(numberslot), o = ChatColor.translateAlternateColorCodes('&', itemsConfig.getStringList("mcmmo vouchers." + itemtype + ".lore").get(skillslot));
				final String type = getSkillName(L.get(skillslot), o);
				int xp = getRemainingInt(input);
				event.setCancelled(true);
				player.updateInventory();

				final HashMap<String, String> replacements = new HashMap<>();
				replacements.put("{XP}", formatInt(xp));
				replacements.put("{AMOUNT}", formatInt(xp));
				replacements.put("{SKILL}", type);
				sendStringListMessage(player, msg, replacements);

				if(xpv)        addRawXP(player, type, xp);
				else if(level) addLevels(player, type, xp);
				else return;
				removeItem(player, it, 1);
				playSound(itemsConfig, "mcmmo vouchers.sounds.redeem " + itemtype, player, player.getLocation(), false);
			}
		}
	}
}