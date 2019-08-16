package me.randomhashtags.randompackage.utils.listeners;

import me.randomhashtags.randompackage.addons.CustomKit;
import me.randomhashtags.randompackage.addons.living.LivingFallenHero;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class KitEvents implements Listener {
    private static KitEvents instance;
    public static KitEvents getKitEvents() {
        if(instance == null) instance = new KitEvents();
        return instance;
    }

    public void unload() {
        HandlerList.unregisterAll(this);
        instance = null;
    }

    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final LivingEntity e = event.getEntity();
        if(!(e instanceof Player)) {
            final HashMap<UUID, LivingFallenHero> L = LivingFallenHero.living;
            if(L != null) {
                final LivingFallenHero f = L.getOrDefault(e.getUniqueId(), null);
                if(f != null) {
                    f.killed(event);
                }
            }
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomKit spawn = CustomKit.valueOfFallenHeroSpawnItem(is), gem = spawn == null ? CustomKit.valueOfFallenHeroGemItem(is) : null;
            final Player player = event.getPlayer();
            if(spawn != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                spawn.getKitClass().trySpawningFallenHero(player, is, spawn, event.getClickedBlock().getLocation());
            } else if(gem != null) {
                gem.getKitClass().tryIncreaseTier(player, is, gem);
            }
        }
    }
}
