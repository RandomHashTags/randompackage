package me.randomhashtags.randompackage.util.listener;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.living.LivingFallenHero;
import me.randomhashtags.randompackage.util.RPStorage;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static me.randomhashtags.randompackage.addon.Kits.PREVIEWING;

public enum KitEvents implements Listener, RPStorage {
    INSTANCE;

    public void unload() {
        for(HumanEntity player : new ArrayList<>(PREVIEWING)) {
            player.closeInventory();
        }
        PREVIEWING = null;
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        if(!(entity instanceof Player)) {
            final HashMap<UUID, LivingFallenHero> living = LivingFallenHero.LIVING;
            if(living != null) {
                final LivingFallenHero fallenHero = living.getOrDefault(entity.getUniqueId(), null);
                if(fallenHero != null) {
                    fallenHero.killed(event);
                }
            }
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomKit spawn = valueOfFallenHeroSpawnItem(is, null), gem = spawn == null ? valueOfFallenHeroGem(is, null) : null;
            final Player player = event.getPlayer();
            if(spawn != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                spawn.getKitClass().trySpawningFallenHero(player, is, spawn, event.getClickedBlock().getLocation());
            } else if(gem != null) {
                gem.getKitClass().tryIncreaseTier(player, is, gem);
            } else {
                return;
            }
            event.setCancelled(true);
            player.updateInventory();
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        PREVIEWING.remove(event.getPlayer());
    }
}
