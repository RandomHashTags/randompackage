package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.addon.RarityGem;
import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.RarityGemData;
import me.randomhashtags.randompackage.event.DepleteRarityGemEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class DepleteRarityGem extends AbstractEventCondition {
    @Override
    public boolean check(@NotNull Entity entity, @NotNull String value) {
        if(entity instanceof Player) {
            final String[] values = value.split(":");
            final RarityGem gem = getRarityGem(values[0]);
            if(gem != null) {
                final FileRPPlayer pdata = FileRPPlayer.get(entity.getUniqueId());
                final RarityGemData rarityGemData = pdata.getRarityGemData();
                if(rarityGemData.isActive(gem)) {
                    final Player player = (Player) entity;
                    final ItemStack rarityGem = getRarityGem(gem, player);
                    if(rarityGem != null) {
                        ItemMeta itemMeta = rarityGem.getItemMeta();
                        int gemAmount = getRemainingInt(itemMeta.getDisplayName());
                        int depleteAmount = (int) evaluate(values[1]);

                        final DepleteRarityGemEvent event = new DepleteRarityGemEvent(player, gem, gemAmount, depleteAmount);
                        PLUGIN_MANAGER.callEvent(event);
                        if(event.isCancelled()) {
                            return false;
                        }
                        gemAmount = event.getGemAmount();
                        depleteAmount = event.getDepleteAmount();

                        /*
                        final FactionUpgrades fu = FactionUpgrades.getFactionUpgrades();
                        if(fu.isEnabled() && hookedFactionsUUID()) {
                            depleteAmount -= depleteAmount*fu.getDecreaseRarityGemPercent(factions.getRegionalIdentifier(u), gem);
                        }*/

                        if(gemAmount-depleteAmount <= 0) {
                            depleteAmount = gemAmount;
                            rarityGemData.toggle(gem, gem.getToggleOffRanOutMsg());
                        }
                        itemMeta = rarityGem.getItemMeta();
                        itemMeta.setDisplayName(gem.getItem().getItemMeta().getDisplayName().replace("{SOULS}", Integer.toString(gemAmount - depleteAmount)));
                        rarityGem.setItemMeta(itemMeta);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
