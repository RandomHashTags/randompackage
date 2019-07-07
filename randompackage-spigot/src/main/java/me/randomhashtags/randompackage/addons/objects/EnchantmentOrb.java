package me.randomhashtags.randompackage.addons.objects;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class EnchantmentOrb {
	public static HashMap<String, EnchantmentOrb> orbs;
	private final ItemStack is;
	private final String path, appliedlore;
	private final List<String> appliesto;
	private final int maxenchants, percentlore, increment;
	public EnchantmentOrb(String path, ItemStack is, String appliedlore, List<String> appliesto, int maxenchants, int increment) {
		if(orbs == null) {
			orbs = new HashMap<>();
		}
		this.path = path;
		this.is = is;
		int q = 0;
		if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore())
			for(int i = 0; i < is.getItemMeta().getLore().size(); i++)
				if(is.getItemMeta().getLore().get(i).contains("{PERCENT}")) 
					q = i;
		percentlore = q;
		this.appliesto = appliesto;
		this.maxenchants = maxenchants;
		this.appliedlore = ChatColor.translateAlternateColorCodes('&', appliedlore.replace("{SLOTS}", Integer.toString(maxenchants).replace("{ADD_SLOTS}", Integer.toString(increment))));
		this.increment = increment;
		orbs.put(path + maxenchants, this);
	}
	public String getPath() { return path; }
	public ItemStack getItem() { return is.clone(); }
	public ItemStack getItem(int percent) {
		final int slot = getPercentLoreSlot();
		final ItemStack i = getItem();
		final ItemMeta m = i.getItemMeta();
		final List<String> L = m.getLore();
		L.set(slot, L.get(slot).replace("{PERCENT}", Integer.toString(percent)));
		m.setLore(L);
		i.setItemMeta(m);
		return i;
	}
	public String getApplyLore() { return appliedlore; }
	public List<String> getAppliesTo() { return appliesto; }
	public int getMaxAllowableEnchants() { return maxenchants; }
	public int getPercentLoreSlot() { return percentlore; }
	public int getIncrement() { return increment; }

	public static EnchantmentOrb valueOf(ItemStack is) {
		if(orbs != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
			final ItemStack item = is.clone();
			final ItemMeta M = item.getItemMeta();
			final List<String> l = M.getLore();
			final int S = l.size();
			for(EnchantmentOrb orb : orbs.values()) {
				final ItemStack its = orb.getItem();
				final ItemMeta m = its.getItemMeta();
				final List<String> L = m.getLore();
				if(L.size() == S) {
					final int slot = orb.getPercentLoreSlot();
					L.set(slot, l.get(slot));
					M.setLore(L);
					its.setItemMeta(M);
					if(is.isSimilar(its))
						return orb;
				}
			}
		}
		return null;
	}
	public static EnchantmentOrb valueOf(String appliedlore) {
		if(orbs != null && appliedlore != null) {
			for(EnchantmentOrb orb : orbs.values())
				if(orb.getApplyLore().equals(appliedlore))
					return orb;
		}
		return null;
	}
	public static boolean hasOrb(ItemStack is) {
		if(orbs != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
			final List<String> l = is.getItemMeta().getLore();
			for(EnchantmentOrb orb : orbs.values())
				if(l.contains(orb.getApplyLore()))
					return true;
			}
		return false;
	}
	public static EnchantmentOrb getOrb(ItemStack is) {
		if(orbs != null && is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
			final List<String> l = is.getItemMeta().getLore();
			for(EnchantmentOrb e : orbs.values())
				if(l.contains(e.getApplyLore()))
					return e;
		}
		return null;
	}

	public static void deleteAll() {
		orbs = null;
	}
}
