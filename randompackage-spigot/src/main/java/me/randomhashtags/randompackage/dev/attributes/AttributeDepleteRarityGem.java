package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.addons.RarityGem;
import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import me.randomhashtags.randompackage.utils.RPPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class AttributeDepleteRarityGem extends AbstractEventAttribute {
    public String getIdentifier() { return "DEPLETERARITYGEM"; }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final String v = recipientValues.get(e);
            if(v != null && e instanceof Player) {
                final String[] s = v.split(":");
                final RarityGem gem = getRarityGem(s[0]);
                if(gem != null) {
                    final RPPlayer pdata = RPPlayer.get(e.getUniqueId());
                    if(pdata.hasActiveRarityGem(gem)) {
                        final Player player = (Player) e;
                        final ItemStack g = getRarityGem(gem, player);
                        if(g != null) {
                            ItemMeta itemMeta = g.getItemMeta();
                            final int amount = getRemainingInt(itemMeta.getDisplayName());
                            int depleteAmount = Integer.parseInt(s[1]);

                            /*
                            final FactionUpgrades fu = FactionUpgrades.getFactionUpgrades();
                            if(fu.isEnabled() && hookedFactionsUUID()) {
                                depleteAmount -= depleteAmount*fu.getDecreaseRarityGemPercent(factions.getRegionalIdentifier(u), gem);
                            }*/

                            if(amount - depleteAmount <= 0) {
                                depleteAmount = amount;
                                pdata.toggleRarityGem(null, gem);
                            }
                            itemMeta = g.getItemMeta();
                            itemMeta.setDisplayName(gem.getItem().getItemMeta().getDisplayName().replace("{SOULS}", Integer.toString(amount - depleteAmount)));
                            g.setItemMeta(itemMeta);
                        }
                    }
                }
            }
        }
    }
}
