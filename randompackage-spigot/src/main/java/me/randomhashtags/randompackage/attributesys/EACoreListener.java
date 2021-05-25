package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.event.*;
import me.randomhashtags.randompackage.event.armor.ArmorEquipEvent;
import me.randomhashtags.randompackage.event.armor.ArmorPieceBreakEvent;
import me.randomhashtags.randompackage.event.armor.ArmorSwapEvent;
import me.randomhashtags.randompackage.event.armor.ArmorUnequipEvent;
import me.randomhashtags.randompackage.event.async.ItemNameTagUseEvent;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantApplyEvent;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.event.enchant.PlayerRevealCustomEnchantEvent;
import me.randomhashtags.randompackage.event.mob.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.event.mob.MobStackDepleteEvent;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EACoreListener extends EventExecutor implements Listener {
    private static EACoreListener instance;
    public static EACoreListener getEACoreListener() {
        if(instance == null) instance = new EACoreListener();
        return instance;
    }

    private static List<EventAttributeListener> eventListeners;

    private Listener v1_9, v1_10, v1_12, v1_13, v1_14;

    @Override
    public String getIdentifier() {
        return "EVENT_ATTRIBUTE_CORE_LISTENER";
    }

    public void load() {
        eventListeners = new ArrayList<>();
        initListeners();
        updateVersionListeners(true);
    }
    public void unload() {
        eventListeners = null;
        updateVersionListeners(false);
    }

    private void initListeners() {
        if(EIGHT) {
            return;
        }
        // 1.11 and 1.15 don't have any version change of events
        v1_9 = new v1_9_Events();
        if(!NINE) {
            v1_10 = new v1_10_Events();
            if(!TEN) {
                //v1_11 = new v1_11_Events();
                if(!ELEVEN) {
                    v1_12 = new v1_12_Events();
                    if(!TWELVE) {
                        v1_13 = new v1_13_Events();
                        if(!THIRTEEN) {
                            v1_14 = new v1_14_Events();
                            if(!FOURTEEN) {
                                //v1_15 = new v1_15_Events();
                            }
                        }
                    }
                }
            }
        }
    }
    private void updateVersionListeners(boolean register) {
        final List<Listener> listeners = new ArrayList<>();
        if(v1_9 != null) {
            listeners.add(v1_9);
        }
        if(v1_10 != null) {
            listeners.add(v1_10);
        }
        if(v1_12 != null) {
            listeners.add(v1_12);
        }
        if(v1_13 != null) {
            listeners.add(v1_13);
        }
        if(v1_14 != null) {
            listeners.add(v1_14);
        }
        for(Listener listener : listeners) {
            if(register) {
                PLUGIN_MANAGER.registerEvents(listener, RANDOM_PACKAGE);
            } else {
                HandlerList.unregisterAll(listener);
            }
        }
    }

    private class v1_9_Events implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        private void playerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) {
            callEventAttributeListeners(event);
        }
    }
    private class v1_10_Events implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        private void playerChangedMainHandEvent(PlayerChangedMainHandEvent event) {
            callEventAttributeListeners(event);
        }
    }
    private class v1_12_Events implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        private void playerAdvancementDoneEvent(PlayerAdvancementDoneEvent event) {
            callEventAttributeListeners(event);
        }
        @EventHandler(priority = EventPriority.HIGHEST)
        private void playerItemMendEvent(PlayerItemMendEvent event) {
            callEventAttributeListeners(event);
        }
    }
    private class v1_13_Events implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        private void playerCommandSendEvent(PlayerCommandSendEvent event) {
            callEventAttributeListeners(event);
        }
        @EventHandler(priority = EventPriority.HIGHEST)
        private void playerRecipeDiscoverEvent(PlayerRecipeDiscoverEvent event) {
            callEventAttributeListeners(event);
        }
        @EventHandler(priority = EventPriority.HIGHEST)
        private void playerRiptideEvent(PlayerRiptideEvent event) {
            callEventAttributeListeners(event);
        }
    }
    private class v1_14_Events implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        private void playerTakeLecternBookEvent(PlayerTakeLecternBookEvent event) {
            callEventAttributeListeners(event);
        }
    }

    public final void registerEventAttributeListener(@NotNull EventAttributeListener listener) {
        if(eventListeners == null) {
            eventListeners = new ArrayList<>();
        }
        if(listener != null && !eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }
    public final void callEventAttributeListeners(@NotNull Event event) {
        if(eventListeners != null) {
            for(EventAttributeListener listener : eventListeners) {
                listener.called(event);
            }
        }
    }
    public final void unregisterEventAttributeListener(EventAttributeListener listener) {
        if(listener != null && eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void creatureSpawnEvent(CreatureSpawnEvent event) {
        if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            final UUID u = event.getEntity().getUniqueId();
            if(!SPAWNED_FROM_SPAWNER.contains(u)) {
                SPAWNED_FROM_SPAWNER.add(u);
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void entityShootBowEvent(EntityShootBowEvent event) {
        PROJECTILE_EVENTS.put(event.getProjectile().getUniqueId(), event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void projectileHitEvent(ProjectileHitEvent event) {
        PROJECTILE_EVENTS.remove(event.getEntity().getUniqueId());
        callEventAttributeListeners(event);
    }

    /*
     * Vanilla Listeners
     */

    @EventHandler(priority = EventPriority.HIGHEST)
    private void asyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockBreakEvent(BlockBreakEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDamageEvent(EntityDamageEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
        callEventAttributeListeners(event);
        SPAWNED_FROM_SPAWNER.remove(event.getEntity().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityShootBowEventListener(EntityShootBowEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityTameEvent(EntityTameEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void foodLevelChangeEvent(FoodLevelChangeEvent event) {
        callEventAttributeListeners(event);
    }

    // Player events
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerAnimationEvent(PlayerAnimationEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerBedEnterEvent(PlayerBedEnterEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerBedLeaveEvent(PlayerBedLeaveEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerChangedWorldEvent(PlayerChangedWorldEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerCommandPreProcessEvent(PlayerCommandPreprocessEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerDropItemEvent(PlayerDropItemEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerExpChangeEvent(PlayerExpGainEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerFishEvent(PlayerFishEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerItemConsumeEvent(PlayerItemConsumeEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerItemDamageEvent(PlayerItemDamageEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerItemHeldEvent(PlayerItemHeldEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerJoinEvent(PlayerJoinEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerPickupItemEvent(PlayerPickupItemEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerQuitEvent(PlayerQuitEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerResourcePackStatusEvent(PlayerResourcePackStatusEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerRespawnEvent(PlayerRespawnEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerShearEntityEvent(PlayerShearEntityEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerStatisticIncrementEvent(PlayerStatisticIncrementEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerToggleFlightEvent(PlayerToggleFlightEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerToggleSneakEvent(PlayerToggleSneakEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerToggleSprintEvent(PlayerToggleSprintEvent event) {
        callEventAttributeListeners(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void projectileLaunchEvent(ProjectileLaunchEvent event) {
        callEventAttributeListeners(event);
    }
    /*
     * RandomPackage Listeners
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void alchemistExchangeEvent(AlchemistExchangeEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void armorEquipEvent(ArmorEquipEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void armorUnequipEvent(ArmorUnequipEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void armorPieceBreakEvent(ArmorPieceBreakEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void armorSwapEvent(ArmorSwapEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blackScrollUseEvent(BlackScrollUseEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerRevealCustomEnchantEvent(PlayerRevealCustomEnchantEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void coinFlipEndEvent(CoinFlipEndEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void customBossDamageByEntityEvent(CustomBossDamageByEntityEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void fundDepositEvent(FundDepositEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void customEnchantProcEvent(CustomEnchantProcEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void customEnchantApplyEvent(CustomEnchantApplyEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void depleteRarityGemEvent(DepleteRarityGemEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void enchanterPurchaseEvent(EnchanterPurchaseEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void equipmentLootboxOpenEvent(EquipmentLootboxOpenEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void itemNameTagUseEvent(ItemNameTagUseEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void jackpotPurchaseTicketsEvent(JackpotPurchaseTicketsEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void mobStackDepleteEvent(MobStackDepleteEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void mysteryMobSpawnerOpenEvent(MysteryMobSpawnerOpenEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void randomizationScrollUseEvent(RandomizationScrollUseEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerClaimEnvoyCrateEvent(PlayerClaimEnvoyCrateEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void serverCrateOpenEvent(ServerCrateOpenEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void shopPurchaseEvent(ShopPurchaseEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void shopSellEvent(ShopSellEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void tinkererTradeEvent(TinkererTradeEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void transmogScrollUseEvent(TransmogScrollUseEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void whiteScrollUseEvent(WhiteScrollUseEvent event) {
        callEventAttributeListeners(event);
    }
}
