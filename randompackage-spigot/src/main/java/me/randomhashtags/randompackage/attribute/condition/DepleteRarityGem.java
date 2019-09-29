package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import me.randomhashtags.randompackage.util.RPPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DepleteRarityGem extends AbstractEventCondition {
    @Override
    public boolean check(Entity e, String value) {
        if(e instanceof Player) {
            final String[] s = value.split(":");
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

                        if(amount-depleteAmount <= 0) {
                            depleteAmount = amount;
                            pdata.toggleRarityGem(gem, gem.getToggleOffRanOutMsg());
                        }
                        itemMeta = g.getItemMeta();
                        itemMeta.setDisplayName(gem.getItem().getItemMeta().getDisplayName().replace("{SOULS}", Integer.toString(amount - depleteAmount)));
                        g.setItemMeta(itemMeta);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
