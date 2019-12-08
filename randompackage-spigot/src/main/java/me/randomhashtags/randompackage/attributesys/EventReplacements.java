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

import java.math.BigDecimal;
import java.util.*;

public interface EventReplacements extends EventEntities {
    default LinkedHashMap<String, String> getReplacements(String...replacements) { return getReplacements((List<String>) null, replacements); }
    default LinkedHashMap<String, String> getReplacements(List<String> addedReplacements, String...replacements) {
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
                if(i%2 == 1) {
                    r.put(addedReplacements.get(i-1), addedReplacements.get(i));
                }
            }
        }
        return !r.isEmpty() ? r : null;
    }

    default String[] getReplacements(Event event) {
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

            case "boostertrigger": return getReplacements((BoosterTriggerEvent) event);
            case "coinflipend": return getReplacements((CoinFlipEndEvent) event);
            case "customenchantproc": return getReplacements((CustomEnchantProcEvent) event);
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
                if(MCMMOAPI.getMCMMOAPI().isEnabled()) {
                    return new String[] {"xp", Float.toString(((com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent) event).getRawXpGained())};
                }

            default: return new String[]{};
        }
    }
    // Bukkit event replacements
    default String[] getReplacements(EntityDeathEvent event) {
        final LivingEntity e = event.getEntity(), k = e.getKiller();
        final boolean NN = k != null;
        final String[] a = new String[] {"xp", Integer.toString(event.getDroppedExp()), "@Victim", toString(e.getLocation())}, b = NN ? new String[]{"@Killer", toString(k.getLocation())} : null;
        return NN ? getReplacements(a, b) : a;
    }
    default String[] getLocationReplacements(Entity entity, String id) {
        final String[] a = new String[]{"@" + id, toString(entity.getLocation())};
        return entity instanceof LivingEntity ?  getReplacements(a, new String[]{"@" + id + "EyeLocation", toString(((LivingEntity) entity).getEyeLocation())}) : a;
    }

    default String[] getProjectileReplacements(Projectile p) { return getReplacements(getLocationReplacements(p, "Projectile"), getLocationReplacements((Entity) p.getShooter(), "Shooter")); }

    default String[] getReplacements(BlockBreakEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"xp", Integer.toString(event.getExpToDrop()), "@Block", toString(event.getBlock().getLocation())}); }
    default String[] getReplacements(BlockGrowEvent event) { return new String[] {"@Block", toString(event.getBlock().getLocation())}; }
    default String[] getReplacements(BlockPlaceEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), (String[]) null); }
    default String[] getReplacements(PlayerFishEvent event) {
        final Entity entity = event.getCaught();
        final String[] replacements = getLocationReplacements(event.getPlayer(), "Player");
        final List<String> added = new ArrayList<>();
        added.add("xp");
        added.add(Integer.toString(event.getExpToDrop()));
        if(entity != null) {
            added.add("@Caught");
            added.add(toString(entity.getLocation()));
        }
        return getReplacements(replacements, added);
    }
    default String[] getReplacements(EntityDamageEvent event) { return getReplacements(getLocationReplacements(event.getEntity(), "Victim"), new String[] {"dmg", Double.toString(event.getDamage())}); }
    default String[] getReplacements(EntityDamageByEntityEvent event) { return new String[] {"dmg", Double.toString(event.getDamage()), "@Damager", toString(event.getDamager().getLocation()), "@Victim", toString(event.getEntity().getLocation())}; }
    default String[] getReplacements(EntityShootBowEvent event) { return new String[] {"@Shooter", toString(event.getEntity().getLocation()), "@Projectile", toString(event.getProjectile().getLocation())}; }
    default String[] getReplacements(EntityTameEvent event) { return getReplacements(getLocationReplacements(event.getEntity(), "Entity"), (String[]) null); }
    default String[] getReplacements(FoodLevelChangeEvent event) {
        final ItemStack is = LEGACY || THIRTEEN ? null : event.getItem();
        final String m = is != null ? is.getType().name() : "AIR";
        return getReplacements(getLocationReplacements(event.getEntity(), "Player"), new String[] {"{ITEM}", UMaterial.match(m).name()});
    }
    default String[] getReplacements(PlayerExpGainEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"xp", Integer.toString(event.getAmount())}); }
    default String[] getReplacements(PlayerInteractEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), (String[]) null); }
    default String[] getReplacements(ProjectileHitEvent event) { return getProjectileReplacements(event.getEntity()); }
    default String[] getReplacements(ProjectileLaunchEvent event) { return getProjectileReplacements(event.getEntity()); }
    // RandomPackage event replacements
    default String[] getReplacements(BoosterTriggerEvent event) {
        final ActiveBooster a = event.booster;
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"multiplier", Double.toString(a.getMultiplier()), "duration", Long.toString(a.getDuration())});
    }
    default String[] getReplacements(CoinFlipEndEvent event) {
        final BigDecimal wager = event.wager, tax = wager.multiply(event.tax), total = wager.subtract(tax);
        return new String[] {"woncash", total.toPlainString(), "wager", wager.toPlainString()};
    }
    default String[] getReplacements(CustomEnchantProcEvent event) {
        final HashMap<String, Entity> e = event.getEntities();
        final String[] a = getReplacements(event.getEvent()), b = new String[] {"@Player", toString(e.get("Player").getLocation()), "level", Integer.toString(event.getEnchantLevel()), "{ENCHANT}", event.getEnchant().getName()};
        return getReplacements(a, b);
    }
    default String[] getReplacements(DamageEvent event) {
        final String[] a = new String[]{"dmg", Double.toString(event.getDamage())};
        final List<String> b = new ArrayList<>();
        final Entity damager = event.getDamager();
        if(damager != null) {
            b.addAll(Arrays.asList("@Damager", toString(damager.getLocation())));
        }
        return getReplacements(a, b);
    }
    default String[] getReplacements(FundDepositEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"amount", event.amount.toString()}); }
    default String[] getReplacements(JackpotPurchaseTicketsEvent event) {
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"moneySpent", event.price.toPlainString(), "tickets", event.amount.toBigInteger().toString()});
    }
    default String[] getReplacements(KitClaimEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"level", Integer.toString(event.getLevel())}); }
    default String[] getReplacements(KitPreClaimEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"chance", Integer.toString(event.getLevelupChance()), "level", Integer.toString(event.getLevel())}); }
    default String[] getReplacements(LootbagClaimEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"size", Integer.toString(event.getRewardSize())}); }
    default String[] getReplacements(PlayerTeleportDelayEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"delay", Double.toString(event.getDelay())}); }
    default String[] getReplacements(ShopEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"total", event.getTotal().toString()}); }
    default String[] getReplacements(TinkererTradeEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"tradesize", Integer.toString(event.trades.size())}); }

    default String[] getReplacements(String[] a, List<String> b) {
        final List<String> c = new ArrayList<>();
        c.addAll(Arrays.asList(a));
        c.addAll(b);
        return c.toArray(new String[a.length+b.size()]);
    }
    default String[] getReplacements(String[] a, String[] b) {
        final List<String> c = new ArrayList<>();
        int al = 0, bl = 0;
        if(a != null) {
            c.addAll(Arrays.asList(a));
            al = a.length;
        }
        if(b != null) {
            c.addAll(Arrays.asList(b));
            bl = b.length;
        }
        return c.toArray(new String[al+bl]);
    }
}
