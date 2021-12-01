package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.event.*;
import me.randomhashtags.randompackage.event.async.ItemLoreCrystalUseEvent;
import me.randomhashtags.randompackage.event.async.ItemNameTagUseEvent;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.event.mob.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.event.mob.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.event.mob.MobStackDepleteEvent;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.HashMap;

public interface EventEntities extends EventConditions, UVersionableSpigot {
    default HashMap<String, Entity> getEntities(Object...values) {
        final HashMap<String, Entity> e = new HashMap<>();
        for(int i = 0; i < values.length; i++) {
            if(i%2 == 1) {
                e.put((String) values[i-1], (Entity) values[i]);
            }
        }
        return e;
    }

    default HashMap<String, Entity> getEntities(Event event) {
        final String name = event.getEventName();
        switch (name.toLowerCase().split("event")[0]) {
            case "entitydeath":
            case "playerdeath": return getEntities((EntityDeathEvent) event);

            case "entitydamage": return getEntities((EntityDamageEvent) event);
            case "entitydamagebyblock": return getEntities("Entity", ((EntityDamageByBlockEvent) event).getEntity());
            case "entitydamagebyentity": return getEntities((EntityDamageByEntityEvent) event);
            case "entityshootbow": return getEntities((EntityShootBowEvent) event);
            case "entitytame": return getEntities((EntityTameEvent) event);
            case "playerfish": return getEntities((PlayerFishEvent) event);

            case "blockbreak": return getEntities("Player", ((BlockBreakEvent) event).getPlayer());

            case "blockmultiplace":
            case "blockplace": return getEntities("Player", ((BlockPlaceEvent) event).getPlayer());

            case "foodlevelchange": return getEntities("Player", ((FoodLevelChangeEvent) event).getEntity());
            case "projectilehit": return getEntities((ProjectileHitEvent) event);
            case "projectilelaunch": return getEntities((ProjectileLaunchEvent) event);

            // RandomPackage Events
            case "isdamaged": return getEntities((isDamagedEvent) event);
            case "pvany": return getEntities((PvAnyEvent) event);
            case "custombossdamagebyentityevent": return getEntities((CustomBossDamageByEntityEvent) event);
            case "coinflipend": return getEntities((CoinFlipEndEvent) event);
            case "customenchantproc": return ((CustomEnchantProcEvent) event).getEntities();
            case "fallenheroslain": return getEntities((FallenHeroSlainEvent) event);
            case "mobstackdeplete": return getEntities((MobStackDepleteEvent) event);
            case "itemnametaguse": return getEntities("Player", ((ItemNameTagUseEvent) event).getPlayer());
            case "itemlorecrystaluse": return getEntities("Player", ((ItemLoreCrystalUseEvent) event).getPlayer());

            case "mcmmoplayerxpgain": return getEntities("Player", ((com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent) event).getPlayer());

            default:
                if(event instanceof RPEvent) {
                    return getEntities((RPEvent) event);
                } else if(event instanceof PlayerEvent) {
                    return getEntities((PlayerEvent) event);
                } else {
                    sendConsoleMessage("&6[RandomPackage] &cMissing Event Entities for event &f" + name);
                    return new HashMap<>();
                }
        }
    }

    default HashMap<String, Entity> getEntities(EntityDeathEvent event) {
        final LivingEntity victim = event.getEntity(), killer = victim.getKiller();
        return getEntities("Victim", victim, "Killer", killer);
    }
    default HashMap<String, Entity> getEntities(EntityDamageEvent event) {
        return getEntities("Victim", event.getEntity());
    }
    default HashMap<String, Entity> getEntities(EntityDamageByEntityEvent event) {
        return getEntities("Damager", event.getDamager(), "Victim", event.getEntity());
    }
    default HashMap<String, Entity> getEntities(EntityShootBowEvent event) {
        return getEntities("Projectile", event.getProjectile(), "Shooter", event.getEntity());
    }
    default HashMap<String, Entity> getEntities(EntityTameEvent event) {
        return getEntities("Entity", event.getEntity(), "Owner", event.getOwner());
    }
    default HashMap<String, Entity> getEntities(PlayerEvent event) {
        return getEntities("Player", event.getPlayer());
    }
    default HashMap<String, Entity> getEntities(PlayerFishEvent event) {
        return getEntities("Player", event.getPlayer(), "Caught", event.getCaught());
    }
    default HashMap<String, Entity> getEntities(ProjectileHitEvent event) {
        final Projectile p = event.getEntity();
        return getEntities("Projectile", p, "Shooter", p.getShooter(), "Victim", getHitEntity(event));
    }
    default HashMap<String, Entity> getEntities(ProjectileLaunchEvent event) {
        final Projectile p = event.getEntity();
        return getEntities("Projectile", p, "Shooter", p.getShooter());
    }
    // RandomPackage event entities
    default HashMap<String, Entity> getEntities(CoinFlipEndEvent event) {
        final HashMap<String, Entity> entities = new HashMap<>();
        final OfflinePlayer winner = event.winner, loser = event.loser;
        if(winner.isOnline()) {
            entities.put("Winner", winner.getPlayer());
        }
        if(loser.isOnline()) {
            entities.put("Loser", loser.getPlayer());
        }
        return entities;
    }
    default HashMap<String, Entity> getEntities(DamageEvent event) {
        return getEntities("Victim", event.getEntity(), "Damager", event.getDamager());
    }
    default HashMap<String, Entity> getEntities(FallenHeroSlainEvent event) {
        return getEntities("Victim", event.hero.getEntity(), "Killer", event.killer);
    }
    default HashMap<String, Entity> getEntities(MobStackDepleteEvent event) {
        return getEntities("Killer", event.killer, "Victim", event.stack.entity);
    }
    default HashMap<String, Entity> getEntities(RPEvent event) {
        return getEntities("Player", event.getPlayer());
    }
}
