package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.addon.living.ActiveBooster;
import me.randomhashtags.randompackage.event.*;
import me.randomhashtags.randompackage.event.booster.BoosterTriggerEvent;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.event.kit.KitClaimEvent;
import me.randomhashtags.randompackage.event.kit.KitPreClaimEvent;
import me.randomhashtags.randompackage.event.lootbag.LootbagClaimEvent;
import me.randomhashtags.randompackage.supported.mechanics.MCMMOAPI;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public interface EventReplacements extends EventEntities {
    default LinkedHashMap<String, String> getReplacements(String...replacements) {
        return getReplacements((List<String>) null, replacements);
    }
    default LinkedHashMap<String, String> getReplacements(@Nullable List<String> addedReplacements, String...replacements) {
        final LinkedHashMap<String, String> r = new LinkedHashMap<>();
        if(replacements != null) {
            for(int i = 0; i < replacements.length; i++) {
                if(i%2 == 1) {
                    r.put(replacements[i-1], replacements[i]);
                }
            }
        }
        if(addedReplacements != null && !addedReplacements.isEmpty()) {
            for(int i = 0; i < addedReplacements.size(); i++) {
                if(i % 2 == 1) {
                    r.put(addedReplacements.get(i-1), addedReplacements.get(i));
                }
            }
        }
        return !r.isEmpty() ? r : null;
    }

    default String[] getReplacements(@NotNull Event event) {
        switch (event.getEventName().toLowerCase().split("event")[0]) {
            case "entitydeath": return getReplacements((EntityDeathEvent) event);
            case "blockbreak": return getReplacements((BlockBreakEvent) event);
            case "blockgrow": return getReplacements((BlockGrowEvent) event);
            case "blockplace": return getReplacements((BlockPlaceEvent) event);
            case "playerfish": return getReplacements((PlayerFishEvent) event);
            case "entitydamage": return getReplacements((EntityDamageEvent) event);
            case "entitydamagebyentity": return getReplacements((EntityDamageByEntityEvent) event);
            case "entityshootbow": return getReplacements((EntityShootBowEvent) event);
            case "entitytame": return getReplacements((EntityTameEvent) event);
            case "foodlevelchange": return getReplacements((FoodLevelChangeEvent) event);

            case "playerexpgain": return getReplacements((PlayerExpGainEvent) event);
            case "playerinteract": return getReplacements((PlayerInteractEvent) event);
            case "projectilehit": return getReplacements((ProjectileHitEvent) event);
            case "projectilelaunch": return getReplacements((ProjectileLaunchEvent) event);

            // RandomPackage replacements
            case "blackscrolluse": return getReplacements((BlackScrollUseEvent) event);

            case "boostertrigger": return getReplacements((BoosterTriggerEvent) event);
            case "coinflipend": return getReplacements((CoinFlipEndEvent) event);
            case "customenchantproc": return getReplacements((CustomEnchantProcEvent) event);
            case "depleteraritygem": return getReplacements((DepleteRarityGemEvent) event);
            case "funddeposit": return getReplacements((FundDepositEvent) event);
            case "jackpotpurchasetickets": return getReplacements((JackpotPurchaseTicketsEvent) event);
            case "kitclaim": return getReplacements((KitClaimEvent) event);
            case "kitpreclaim": return getReplacements((KitPreClaimEvent) event);
            case "playerteleportdelay": return getReplacements((PlayerTeleportDelayEvent) event);
            case "tinkerertrade": return getReplacements((TinkererTradeEvent) event);

            case "isdamaged":
            case "pvany": return getReplacements((DamageEvent) event);

            case "dungeonlootbagclaim":
            case "kothlootbagclaim": return getReplacements((LootbagClaimEvent) event);

            case "shoppurchase":
            case "shopsell": return getReplacements((ShopEvent) event);

            case "mcmmoplayerxpgain":
                if(MCMMOAPI.INSTANCE.isEnabled()) {
                    return new String[] {"xp", Float.toString(((com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent) event).getRawXpGained())};
                }

            default:
                if(event instanceof RPEvent) {
                    return getReplacements((RPEvent) event);
                }
                return new String[]{};
        }
    }
    // Bukkit event replacements
    default String[] getReplacements(@NotNull EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity(), killer = entity.getKiller();
        final boolean NN = killer != null;
        final String[] a = new String[] {"xp", Integer.toString(event.getDroppedExp()), "@Victim", location_to_string(entity.getLocation())}, b = NN ? new String[]{"@Killer", location_to_string(killer.getLocation())} : null;
        return NN ? getReplacements(a, b) : a;
    }
    default String[] getLocationReplacements(@NotNull Entity entity, @NotNull String id) {
        final String[] a = new String[]{"@" + id, location_to_string(entity.getLocation())};
        return entity instanceof LivingEntity ?  getReplacements(a, new String[]{"@" + id + "EyeLocation", location_to_string(((LivingEntity) entity).getEyeLocation())}) : a;
    }

    default String[] getProjectileReplacements(@NotNull Projectile p) {
        final ProjectileSource shooter = p.getShooter();
        final String[] shooterReplacements = shooter != null ? getLocationReplacements((Entity) shooter, "Shooter") : null;
        return getReplacements(getLocationReplacements(p, "Projectile"), shooterReplacements);
    }

    default String[] getReplacements(@NotNull BlockBreakEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"xp", Integer.toString(event.getExpToDrop()), "@Block", location_to_string(event.getBlock().getLocation())});
    }
    default String[] getReplacements(@NotNull BlockGrowEvent event) {
        return new String[] {"@Block", location_to_string(event.getBlock().getLocation())};
    }
    default String[] getReplacements(@NotNull BlockPlaceEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), (String[]) null);
    }
    default String[] getReplacements(@NotNull PlayerFishEvent event) {
        final Entity entity = event.getCaught();
        final String[] replacements = getLocationReplacements(event.getPlayer(), "Player");
        final List<String> added = new ArrayList<>();
        added.add("xp");
        added.add(Integer.toString(event.getExpToDrop()));
        if(entity != null) {
            added.add("@Caught");
            added.add(location_to_string(entity.getLocation()));
        }
        return getReplacements(replacements, added);
    }
    default String[] getReplacements(@NotNull EntityDamageEvent event) {
        return getReplacements(getLocationReplacements(event.getEntity(), "Victim"), new String[] {"dmg", Double.toString(event.getDamage())});
    }
    default String[] getReplacements(@NotNull EntityDamageByEntityEvent event) {
        return new String[] {"dmg", Double.toString(event.getDamage()), "@Damager", location_to_string(event.getDamager().getLocation()), "@Victim", location_to_string(event.getEntity().getLocation())};
    }
    default String[] getReplacements(@NotNull EntityShootBowEvent event) {
        return new String[] {"@Shooter", location_to_string(event.getEntity().getLocation()), "@Projectile", location_to_string(event.getProjectile().getLocation())};
    }
    default String[] getReplacements(@NotNull EntityTameEvent event) {
        return getReplacements(getLocationReplacements(event.getEntity(), "Entity"), (String[]) null);
    }
    default String[] getReplacements(@NotNull FoodLevelChangeEvent event) {
        final ItemStack is = LEGACY || THIRTEEN ? null : event.getItem();
        final String m = is != null ? is.getType().name() : "AIR";
        return getReplacements(getLocationReplacements(event.getEntity(), "Player"), new String[] {"{ITEM}", UMaterial.match(m).name()});
    }
    default String[] getReplacements(@NotNull PlayerExpGainEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"xp", Integer.toString(event.getAmount())});
    }
    default String[] getReplacements(@NotNull PlayerInteractEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), (String[]) null);
    }
    default String[] getReplacements(@NotNull ProjectileHitEvent event) {
        return getProjectileReplacements(event.getEntity());
    }
    default String[] getReplacements(@NotNull ProjectileLaunchEvent event) {
        return getProjectileReplacements(event.getEntity());
    }
    // RandomPackage event replacements
    default String[] getReplacements(@NotNull RPEvent event) {
        return getLocationReplacements(event.getPlayer(), "Player");
    }

    default String[] getReplacements(@NotNull BlackScrollUseEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"rate", Integer.toString(event.getSuccessRate())});
    }
    default String[] getReplacements(@NotNull BoosterTriggerEvent event) {
        final ActiveBooster a = event.booster;
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"multiplier", Double.toString(a.getMultiplier()), "duration", Long.toString(a.getDuration())});
    }
    default String[] getReplacements(@NotNull CoinFlipEndEvent event) {
        final BigDecimal wager = event.wager, tax = wager.multiply(event.tax), total = wager.subtract(tax);
        return new String[] {"woncash", total.toPlainString(), "wager", wager.toPlainString()};
    }
    default String[] getReplacements(@NotNull CustomEnchantProcEvent event) {
        final String[] a = getReplacements(event.getEvent()), b = new String[] { "@Player", location_to_string(event.getHolder().getLocation()), "level", Integer.toString(event.getEnchantLevel()), "{ENCHANT}", getLocalizedName(event.getEnchant()) };
        return getReplacements(a, b);
    }
    default String[] getReplacements(@NotNull DepleteRarityGemEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"gemAmount", Integer.toString(event.getGemAmount()), "depleteAmount", Integer.toString(event.getDepleteAmount())});
    }

    default String[] getReplacements(@NotNull DamageEvent event) {
        final String[] a = new String[]{"dmg", Double.toString(event.getDamage())};
        final List<String> b = new ArrayList<>();
        final Entity damager = event.getDamager();
        if(damager != null) {
            b.addAll(List.of("@Damager", location_to_string(damager.getLocation())));
        }
        return getReplacements(a, b);
    }

    default String[] getReplacements(@NotNull FundDepositEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"amount", event.amount.toString()});
    }
    default String[] getReplacements(@NotNull JackpotPurchaseTicketsEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"moneySpent", event.price.toPlainString(), "tickets", event.amount.toString()});
    }
    default String[] getReplacements(@NotNull KitClaimEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"level", Integer.toString(event.getLevel())});
    }
    default String[] getReplacements(@NotNull KitPreClaimEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"chance", Integer.toString(event.getLevelupChance()), "level", Integer.toString(event.getLevel())});
    }
    default String[] getReplacements(@NotNull LootbagClaimEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"size", Integer.toString(event.getRewardSize())});
    }
    default String[] getReplacements(@NotNull PlayerTeleportDelayEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"delay", Double.toString(event.getDelay())});
    }
    default String[] getReplacements(@NotNull ShopEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"total", event.getTotal().toString()});
    }
    default String[] getReplacements(@NotNull TinkererTradeEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"tradesize", Integer.toString(event.trades.size())});
    }

    default String[] getReplacements(@NotNull String[] a, @NotNull List<String> b) {
        final List<String> c = new ArrayList<>();
        c.addAll(List.of(a));
        c.addAll(b);
        return c.toArray(new String[a.length+b.size()]);
    }
    default String[] getReplacements(@NotNull String[] a, @NotNull String[] b) {
        final List<String> list = new ArrayList<>();
        int al = 0, bl = 0;
        if(a != null) {
            list.addAll(List.of(a));
            al = a.length;
        }
        if(b != null) {
            list.addAll(List.of(b));
            bl = b.length;
        }
        return list.toArray(new String[al+bl]);
    }
}
