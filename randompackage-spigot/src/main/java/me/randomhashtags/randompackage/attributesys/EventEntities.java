package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.event.*;
import me.randomhashtags.randompackage.event.async.*;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.event.mob.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.event.mob.MobStackDepleteEvent;
import me.randomhashtags.randompackage.util.universal.UVersionable;
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

public interface EventEntities extends EventConditions, UVersionable {
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
            case "blockplace": return getEntities("Player", ((BlockPlaceEvent) event).getPlayer());
            case "foodlevelchange": return getEntities("Player", ((FoodLevelChangeEvent) event).getEntity());

            case "playeradvancementdone":
            case "playeranimation":
            case "playerbedenter":
            case "playerbedleave":
            case "playerbucketempty":
            case "playerbucketfill":
            case "playerchangedmainhand":
            case "playerchangedworld":
            case "playercommandpreprocess":
            case "playercommandsend":
            case "playerdropitem":
            case "playereditbook":
            case "playerexpchange":
            case "playergamemodechange":
            case "playerinteract":
            case "playeritembreak":
            case "playeritemconsume":
            case "playeritemdamage":
            case "playeritemheld":
            case "playeritemmend":
            case "playerjoin":
            case "playerkick":
            case "playerlevelchange":
            case "playerlocalechange":
            case "playerlogin":
            case "playermove":
            case "playerportal":
            case "playerquit":
            case "playerrecipediscover":
            case "playerresourcepackstatus":
            case "playerrespawn":
            case "playerriptide":
            case "playerstatisticincrement":
            case "playerswaphanditems":
            case "playertakelecternbook":
            case "playerteleport":
            case "playertogglesneak":
            case "playertogglesprint":
            case "playervelocity": return getEntities((PlayerEvent) event);

            case "projectilehit": return getEntities((ProjectileHitEvent) event);
            case "projectilelaunch": return getEntities((ProjectileLaunchEvent) event);

            case "isdamaged": return getEntities((isDamagedEvent) event);
            case "pvany": return getEntities((PvAnyEvent) event);

            case "armorequip":
            case "armorpiecebreak":
            case "armorswap":
            case "armorunequip":

            case "boosteractivate":
            case "boosterexpire":
            case "boosterpreactivate":
            case "boostertrigger":

            case "alchemistexchange":
            case "customenchantapply":
            case "enchanterpurchase":
            case "playerpreapplycustomenchant":
            case "playerrevealcustomenchant":
            case "trinkerertrade":

            case "kitclaim":
            case "kitpreclaim":

            case "dungeonlootbagclaim":
            case "kothlootbagclaim":

            case "customenchanttimer":

            case "armorsetequip":
            case "armorsetunequip":
            case "conquestblockdamage":
            case "factionupgradelevelup":
            case "foodlevellost":
            case "funddeposit":
            case "globalchallengebegin":
            case "globalchallengeend":
            case "globalchallengeparticipate":
            case "jackpotpurchasetickets":
            case "kothcapture":
            case "maskapply":
            case "maskequip":
            case "maskunequip":
            case "mysterymobspawneropen":
            case "playerclaimenvoycrate":
            case "playerexpgain":
            case "playerquestcomplete":
            case "playerquestexpire":
            case "playerqueststart":
            case "playerteleportdelay":
            case "randomizationscrolluse":
            case "servercrateclose":
            case "servercrateopen":
            case "shoppurchase":
            case "shopsell": return getEntities((RPEvent) event);

            case "coinflipend": return getEntities((CoinFlipEndEvent) event);

            case "customenchantproc": return ((CustomEnchantProcEvent) event).getEntities();
            case "fallenheroslain": return getEntities((FallenHeroSlainEvent) event);
            case "mobstackdeplete": return getEntities((MobStackDepleteEvent) event);

            case "itemnametaguse": return getEntities("Player", ((ItemNameTagUseEvent) event).getPlayer());
            case "itemlorecrystaluse": return getEntities("Player", ((ItemLoreCrystalUseEvent) event).getPlayer());

            default:
                sendConsoleMessage("&6[RandomPackage] &cMissing Event Entities for event &f" + name);
                return new HashMap<>();
        }
    }

    default HashMap<String, Entity> getEntities(EntityDeathEvent event) {
        final LivingEntity v = event.getEntity(), k = v.getKiller();
        final HashMap<String, Entity> e = getEntities("Victim", v);
        if(k != null) e.put("Killer", k);
        return e;
    }
    default HashMap<String, Entity> getEntities(EntityDamageEvent event) { return getEntities("Victim", event.getEntity()); }
    default HashMap<String, Entity> getEntities(EntityDamageByEntityEvent event) { return getEntities("Damager", event.getDamager(), "Victim", event.getEntity()); }
    default HashMap<String, Entity> getEntities(EntityShootBowEvent event) { return getEntities("Projectile", event.getProjectile(), "Shooter", event.getEntity()); }
    default HashMap<String, Entity> getEntities(EntityTameEvent event) { return getEntities("Entity", event.getEntity(), "Owner", event.getOwner()); }
    default HashMap<String, Entity> getEntities(PlayerEvent event) { return getEntities("Player", event.getPlayer()); }
    default HashMap<String, Entity> getEntities(PlayerFishEvent event) { return getEntities("Player", event.getPlayer(), "Caught", event.getCaught()); }
    default HashMap<String, Entity> getEntities(ProjectileHitEvent event) {
        final Projectile p = event.getEntity();
        return getEntities("Projectile", p, "Shooter", p.getShooter(), "Victim", getHitEntity(event));
    }
    default HashMap<String, Entity> getEntities(ProjectileLaunchEvent event) {
        final Projectile p = event.getEntity();
        return getEntities("Projectile", p, "Shooter", p.getShooter());
    }
    // RandomPackage event entities
    default HashMap<String, Entity> getEntities(CoinFlipEndEvent event) { return getEntities("Winner", event.winner, "Loser", event.loser); }
    default HashMap<String, Entity> getEntities(DamageEvent event) { return getEntities("Victim", event.getEntity(), "Damager", event.getDamager()); }
    default HashMap<String, Entity> getEntities(FallenHeroSlainEvent event) { return getEntities("Victim", event.hero.getEntity(), "Killer", event.killer); }
    default HashMap<String, Entity> getEntities(MobStackDepleteEvent event) { return getEntities("Killer", event.killer, "Victim", event.stack.entity); }
    default HashMap<String, Entity> getEntities(RPEvent event) { return getEntities("Player", event.getPlayer()); }
}
