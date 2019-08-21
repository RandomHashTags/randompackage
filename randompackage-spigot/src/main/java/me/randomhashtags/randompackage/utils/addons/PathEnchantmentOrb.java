package me.randomhashtags.randompackage.utils.addons;

import me.randomhashtags.randompackage.addons.EnchantmentOrb;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PathEnchantmentOrb extends RPAddon implements EnchantmentOrb {
	private final ItemStack is;
	private final String path, appliedlore;
	private final List<String> appliesto;
	private final int maxenchants, percentlore, increment;
	public PathEnchantmentOrb(String path, ItemStack is, String appliedlore, List<String> appliesto, int maxenchants, int increment) {
		this.path = path;
		this.is = is;
		int q = 0;
		if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
			final List<String> l = is.getItemMeta().getLore();
			for(int i = 0; i < l.size(); i++)
				if(l.get(i).contains("{PERCENT}"))
					q = i;
		}
		percentlore = q;

		this.appliesto = appliesto;
		this.maxenchants = maxenchants;
		this.appliedlore = ChatColor.translateAlternateColorCodes('&', appliedlore.replace("{SLOTS}", Integer.toString(maxenchants).replace("{ADD_SLOTS}", Integer.toString(increment))));
		this.increment = increment;
		addEnchantmentOrb(path + maxenchants, this);
	}
	public String getIdentifier() { return path; }

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
	public String getApplied() { return appliedlore; }
	public List<String> getAppliesTo() { return appliesto; }
	public int getMaxAllowableEnchants() { return maxenchants; }
	public int getPercentLoreSlot() { return percentlore; }
	public int getIncrement() { return increment; }

	public int getMinPercent() { return 0; }
	public int getMaxPercent() { return 100; }

	public boolean canBeApplied(ItemStack itemstack) {
		if(itemstack != null && !itemstack.getType().equals(Material.AIR)) {
			final EnchantmentOrb orb = valueOfEnchantmentOrb(itemstack);
			if(orb != null) {
				final String c = itemstack.getType().name().toLowerCase();
				final EnchantmentOrb o = getEnchantmentOrb(itemstack);
				for(String s : orb.getAppliesTo()) {
					if((o == null || !o.equals(orb) && o.getIncrement() < orb.getIncrement()) && c.endsWith(s.toLowerCase())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
