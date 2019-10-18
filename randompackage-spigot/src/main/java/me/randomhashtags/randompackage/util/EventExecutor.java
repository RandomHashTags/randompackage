package me.randomhashtags.randompackage.util;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.EventAttribute;
import me.randomhashtags.randompackage.addon.living.ActiveBooster;
import me.randomhashtags.randompackage.addon.util.EventReplacer;
import me.randomhashtags.randompackage.event.*;
import me.randomhashtags.randompackage.event.booster.BoosterTriggerEvent;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.event.PvAnyEvent;
import me.randomhashtags.randompackage.event.TinkererTradeEvent;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import me.randomhashtags.randompackage.event.kit.KitClaimEvent;
import me.randomhashtags.randompackage.event.kit.KitPreClaimEvent;
import me.randomhashtags.randompackage.event.lootbag.LootbagClaimEvent;
import me.randomhashtags.randompackage.event.mob.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.event.mob.MobStackDepleteEvent;
import me.randomhashtags.randompackage.event.JackpotPurchaseTicketsEvent;
import me.randomhashtags.randompackage.event.PlayerTeleportDelayEvent;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class EventExecutor extends EventConditions implements EventReplacer {
    private boolean hasReplacements(List<String> conditions) {
        for(String s : conditions) {
            String l = s.toLowerCase();
            if(l.contains("=")) l = l.split("=")[1];
            if(l.startsWith("get") && (l.contains("combo") || l.contains("multiplier") || l.contains("hp") || l.endsWith("saturation") || l.contains("exp") || l.contains("loc"))) {
                return true;
            }
        }
        return false;
    }
    private String doReplacements(HashMap<String, Entity> entities, Set<String> keys, String string) {
        // TODO: add more replacements
        for(String entity : keys) {
            final Entity E = entities.get(entity);
            final boolean isLiving = E instanceof LivingEntity, isPlayer = isLiving && E instanceof Player;
            final LivingEntity le = isLiving ? (LivingEntity) E : null;
            final Player player = isPlayer ? (Player) E : null;
            string = string.replace("get" + entity + "MaxHP", isLiving ? Double.toString(le.getMaxHealth()) : "0");
            string = string.replace("get" + entity + "HP", isLiving ? Double.toString(le.getHealth()) : "0");
            string = string.replace("get" + entity + "Saturation", isPlayer ? Float.toString(player.getSaturation()) : "0");
            if(string.contains("loc")) {
                final Location l = E.getLocation();
                string = string.replace("get" + entity + "LocX", Double.toString(l.getX()));
                string = string.replace("get" + entity + "LocY", Double.toString(l.getY()));
                string = string.replace("get" + entity + "LocZ", Double.toString(l.getZ()));
            }
            if(string.contains("exp")) {
                string = string.replace("get" + entity + "Exp", isPlayer ? Integer.toString(getTotalExperience(player)) : "0");
                string = string.replace("get" + entity + "ExpLevel", isPlayer ? Integer.toString(player.getLevel()) : "0");
            }
            final boolean hasCombo = string.contains("Combo(");
            if(hasCombo || string.contains("Multiplier(")) {
                final UUID u = E.getUniqueId();
                final String combo = string.split("\\(")[1].split("\\)")[0];
                string = string.replace("get" + entity + (hasCombo ? "Combo" : "Multiplier") + "(" + combo.toLowerCase() + ")", isPlayer ? Double.toString(getCombo(u, combo)) : "1");
            }
        }
        return string;
    }

    public boolean didPassConditions(Event event, HashMap<String, Entity> entities, List<String> conditions, HashMap<String, String> valueReplacements, boolean cancelled) {
        boolean passed = true, hasCancelled = false;

        final boolean isInteract = event instanceof PlayerInteractEvent;

        outerloop: for(String c : conditions) {
            final String condition = c.toLowerCase();
            final Set<String> keys = entities.keySet();
            for(String s : keys) {
                String value = c.contains("=") ? doReplacements(entities, keys, c.split("=")[1]) : "false";
                final Entity e = entities.get(s);
                s = s.toLowerCase();
                for(String r : valueReplacements.keySet()) {
                    s = s.replace(r.toLowerCase(), valueReplacements.get(r));
                }
                if(hasReplacements(conditions)) {
                    doReplacements(entities, keys, s);
                }
                if(condition.startsWith("cancelled=")) {
                    passed = cancelled == Boolean.parseBoolean(value);
                    hasCancelled = true;
                } else if(condition.startsWith("chance=")) {
                    passed = random.nextInt(100) < evaluate(value);
                } else {
                    passed = (hasCancelled || !cancelled || isInteract) && passedAllConditions(event, e, condition, s, value, LEGACY, EIGHT, NINE, TEN, ELEVEN, THIRTEEN);
                }
                if(!passed) break outerloop;
            }
        }
        return passed;
    }

    protected HashMap<String, Entity> getEntities(Object...values) {
        final HashMap<String, Entity> e = new HashMap<>();
        for(int i = 0; i < values.length; i++) {
            if(i%2 == 1) {
                e.put((String) values[i-1], (Entity) values[i]);
            }
        }
        return e;
    }

    protected LinkedHashMap<String, String> getReplacements(String...replacements) { return getReplacements((List<String>) null, replacements); }
    protected LinkedHashMap<String, String> getReplacements(List<String> addedReplacements, String...replacements) {
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
    public HashMap<String, Entity> getNearbyEntities(Location center, double radius) { return getNearbyEntities(center, radius, radius, radius); }
    public HashMap<String, Entity> getNearbyEntities(Location center, double radiusX, double radiusY, double radiusZ) {
        final HashMap<String, Entity> e = new HashMap<>();
        final List<Entity> nearby = new ArrayList<>(center.getWorld().getNearbyEntities(center, radiusX, radiusY, radiusZ));
        for(int i = 0; i < nearby.size(); i++) {
            e.put("Nearby" + i, nearby.get(i));
        }
        return e;
    }
    private HashMap<String, Entity> getNearbyType(Player player, double radiusX, double radiusY, double radiusZ, String type) {
        final UUID u = player.getUniqueId();
        final List<UUID> t = type.equals("ALLY") ? regions.getAllies(u) : type.equals("ENEMY") ? regions.getEnemies(u) : type.equals("TRUCE") ? regions.getTruces(u) : regions.getAssociates(u);
        final HashMap<String, Entity> n = new HashMap<>();
        final HashMap<String, Entity> nearby = getNearbyEntities(player.getLocation(), radiusX, radiusY, radiusZ);
        for(String s : nearby.keySet()) {
            final Entity entity = nearby.get(s);
            if(entity instanceof Player && t.contains(entity.getUniqueId())) {
                n.put(s, entity);
            }
        }
        return n;
    }

    private HashMap<RPPlayer, String> getData(HashMap<String, Entity> entities, HashMap<String, String> entityValues) {
        final HashMap<RPPlayer, String> a = new HashMap<>();
        for(String s : entities.keySet()) {
            if(entityValues.containsKey(s)) {
                final Entity e = entities.get(s);
                if(e instanceof Player) {
                    a.put(RPPlayer.get(e.getUniqueId()), entityValues.get(s));
                }
            }
        }
        return a;
    }

    public boolean executeAll(Event event, HashMap<String, Entity> entities, List<String> conditions, boolean cancelled, HashMap<String, String> entityValues, List<LinkedHashMap<EventAttribute, HashMap<Entity, String>>> values) {
        return executeAll(event, entities, conditions, cancelled, entityValues, values, new HashMap<>());
    }
    public boolean executeAll(Event event, HashMap<String, Entity> entities, List<String> conditions, boolean cancelled, HashMap<String, String> entityValues, List<LinkedHashMap<EventAttribute, HashMap<Entity, String>>> values, HashMap<String, String> valueReplacements) {
        return executeAll(event, entities, conditions, cancelled, entityValues, values, valueReplacements, 0);
    }
    public boolean executeAll(Event event, HashMap<String, Entity> entities, List<String> conditions, boolean cancelled, HashMap<String, String> entityValues, List<LinkedHashMap<EventAttribute, HashMap<Entity, String>>> values, HashMap<String, String> valueReplacements, int repeatID) {
        boolean passed = didPassConditions(event, entities, conditions, valueReplacements, cancelled);
        if(passed) {
            final Entity entity1 = entities.getOrDefault("Player", entities.getOrDefault("Killer", entities.getOrDefault("Damager", entities.getOrDefault("Owner", null))));
            final Entity entity2 = entities.getOrDefault("Victim", entities.getOrDefault("Entity", null));
            final HashMap<RPPlayer, String> data = getData(entities, entityValues);
            final boolean dadda = !data.isEmpty(), entity1NN = entity1 != null, entity2NN = entity2 != null;
            final String repeatid = Integer.toString(repeatID);
            final List<LinkedHashMap<EventAttribute, HashMap<Entity, String>>> previousHashMaps = new ArrayList<>();
            final Set<String> entityKeys = entities.keySet();
            attributeLooper: for(LinkedHashMap<EventAttribute, HashMap<Entity, String>> hashmap : values) {
                for(EventAttribute a : hashmap.keySet()) {
                    if(!a.isCancelled()) {
                        final HashMap<Entity, String> valuez = hashmap.get(a);
                        String defaultValue = valuez.getOrDefault(null, null);
                        if(defaultValue != null) {
                            defaultValue = defaultValue.replace("RepeatID", repeatid);
                        }
                        switch (a.getIdentifier()) {
                            case "WAIT":
                                final int ticks = (int) evaluate(replaceValue(defaultValue, valueReplacements));
                                List<LinkedHashMap<EventAttribute, HashMap<Entity, String>>> attributes = new ArrayList<>(values);
                                attributes.removeAll(previousHashMaps);
                                attributes.remove(hashmap);
                                scheduler.scheduleSyncDelayedTask(randompackage, () -> executeAll(event, entities, conditions, cancelled, entityValues, attributes, valueReplacements), ticks);
                                break attributeLooper;
                            case "REPEAT":
                                attributes = new ArrayList<>(values);
                                attributes.removeAll(previousHashMaps);
                                attributes.remove(hashmap);
                                for(int i = 1; i <= evaluate(defaultValue); i++) {
                                    executeAll(event, entities, conditions, cancelled, entityValues, attributes, valueReplacements, i);
                                }
                                break attributeLooper;
                            case "RETURN":
                                passed = Boolean.parseBoolean(defaultValue);
                                if(!passed) {
                                    break attributeLooper;
                                }
                            default:
                                previousHashMaps.add(hashmap);
                                a.execute(event);
                                if(dadda) a.executeData(data, valueReplacements);
                                valuez.remove(null);
                                for(Entity e : valuez.keySet()) {
                                    final String og = valuez.get(e);
                                    if(og != null) {
                                        valuez.put(e, doReplacements(entities, entityKeys, og));
                                    }
                                }
                                a.execute(event, valuez);
                                a.execute(event, valuez, valueReplacements);
                                if(defaultValue != null) {
                                    a.execute(event, defaultValue);
                                    a.execute(event, defaultValue, valueReplacements);
                                    if(entity1NN && entity2NN) {
                                        a.execute(entity1, entity2, defaultValue);
                                    }
                                }
                        }
                    }
                }
            }
        }
        return passed;
    }

    private boolean tryGeneric(Event event, HashMap<String, Entity> entities, List<String> attributes) {
        return tryGeneric(event, entities, attributes, new LinkedHashMap<>());
    }
    private boolean tryGeneric(Event event, HashMap<String, Entity> entities, List<String> attributes, HashMap<String, String> valueReplacements) {
        if(event != null && attributes != null && !attributes.isEmpty()) {
            final List<Boolean> checks = new ArrayList<>();
            final String e = event.getEventName().split("Event")[0].toLowerCase();
            final boolean cancellable = event instanceof Cancellable, cancelled = cancellable && ((Cancellable) event).isCancelled();
            final List<String> entityKeys = new ArrayList<>(entities.keySet());
            for(String s : attributes) {
                final String[] semi = s.split(";");
                final String first = semi[0].toLowerCase();
                if(first.equals(e)) {
                    final List<String> conditions = new ArrayList<>();
                    final HashMap<String, String> entityValues = new HashMap<>();
                    final List<LinkedHashMap<EventAttribute, HashMap<Entity, String>>> execute = new ArrayList<>();

                    for(String c : s.split(semi[0] + ";")[1].split(";")) {
                        if(c.contains("=")) {
                            final String[] values = c.split("="), fvalues = values[0].split("\\(");
                            final String value1 = values[1];
                            String string = values[0].toUpperCase();
                            for(String entity : entityKeys) {
                                final String E = entity.toUpperCase();
                                if(string.startsWith("NEARBY" + E)) {
                                    final String radius = fvalues[1].split("\\)")[0];
                                    final boolean ally = string.contains(E + "ALLIES"), enemy = string.contains(E + "ENEMIES"), truce = string.contains(E + "TRUCES"), member = string.contains(E + "MEMBERS");
                                    if(ally || enemy || truce || member) {
                                        string = string.replace("NEARBY" + E + (ally ? "ALLIES" : enemy ? "ENEMIES" : truce ? "TRUCES" : "MEMBERS") + "(" + radius + ")", "");
                                        final Entity en = entities.get(entity);
                                        entities.remove(entity);
                                        if(en instanceof Player) {
                                            final Player player = (Player) en;
                                            final String[] a = radius.split(":");
                                            final int length = a.length;
                                            final double x = evaluate(a[0]), y = length >= 2 ? evaluate(a[1]) : x, z = length >= 3 ? evaluate(a[2]) : x;
                                            final HashMap<String, Entity> nearby = getNearbyType(player, x, y, z, ally ? "ALLY" : enemy ? "ENEMY" : truce ? "TRUCE" : "MEMBER");
                                            entities.putAll(nearby);
                                            for(String n : nearby.keySet()) {
                                                entityValues.put(n, value1);
                                            }
                                        }
                                    }
                                } else if(string.contains(E)) {
                                    string = string.replace(E, "");
                                    entityValues.put(entity, value1);
                                }
                            }
                            final EventAttribute a = getEventAttribute(string);
                            if(a == null) {
                                conditions.add(c);
                            } else {
                                final LinkedHashMap<EventAttribute, HashMap<Entity, String>> z = new LinkedHashMap<>();
                                z.put(a, new LinkedHashMap<>());
                                final HashMap<Entity, String> E = z.get(a);
                                for(Entity entity : entities.values()) {
                                    E.put(entity, value1);
                                }
                                E.put(null, value1);
                                for(String ss : entities.keySet()) {
                                    E.put(entities.get(ss), entityValues.get(ss));
                                }
                                execute.add(z);
                            }
                        }
                    }
                    checks.add(executeAll(event, entities, conditions, cancelled, entityValues, execute, valueReplacements));
                }
            }
            return checks.contains(true);
        }
        return false;
    }

    private boolean didPass(Event event, List<String> attributes) {
        return event != null && attributes != null && !attributes.isEmpty();
    }
    public boolean trigger(Event event, List<String> attributes, String...replacements) {
        final List<String> c = new ArrayList<>();
        final String[] a = getReplacements(event);
        c.addAll(Arrays.asList(replacements));
        c.addAll(Arrays.asList(a));
        final String[] d = c.toArray(new String[replacements.length+a.length]);
        return trigger(event, getEntities(event), attributes, d);
    }
    public boolean trigger(Event event, HashMap<String, Entity> entities, List<String> attributes, String...replacements) {
        if(didPass(event, attributes) && entities != null) {
            return tryGeneric(event, entities, attributes, getReplacements(replacements));
        }
        return false;
    }

    public HashMap<String, Entity> getEntities(Event event) {
        switch (event.getEventName().toLowerCase().split("event")[0]) {
            case "blockbreak": return getEntities((BlockBreakEvent) event);
            case "blockplace": return getEntities((BlockPlaceEvent) event);
            case "entitydeath": return getEntities((EntityDeathEvent) event);
            case "entitydamage": return getEntities((EntityDamageEvent) event);
            case "entitydamagebyentity": return getEntities((EntityDamageByEntityEvent) event);
            case "entityshootbow": return getEntities((EntityShootBowEvent) event);
            case "entitytame": return getEntities((EntityTameEvent) event);
            case "foodlevelchange": return getEntities((FoodLevelChangeEvent) event);
            case "playerfish": return getEntities((PlayerFishEvent) event);

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
            case "playerdeath":
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

            default: return new HashMap<>();
        }
    }

    public HashMap<String, Entity> getEntities(BlockPlaceEvent event) { return getEntities("Player", event.getPlayer()); }
    public HashMap<String, Entity> getEntities(BlockBreakEvent event) { return getEntities("Player", event.getPlayer()); }
    public HashMap<String, Entity> getEntities(EntityDeathEvent event) {
        final LivingEntity v = event.getEntity(), k = v.getKiller();
        final HashMap<String, Entity> e = getEntities("Victim", v);
        if(k != null) e.put("Killer", k);
        return e;
    }
    public HashMap<String, Entity> getEntities(EntityDamageEvent event) { return getEntities("Victim", event.getEntity()); }
    public HashMap<String, Entity> getEntities(EntityDamageByEntityEvent event) { return getEntities("Damager", event.getDamager(), "Victim", event.getEntity()); }
    public HashMap<String, Entity> getEntities(EntityShootBowEvent event) { return getEntities("Projectile", event.getProjectile(), "Shooter", event.getEntity()); }
    public HashMap<String, Entity> getEntities(EntityTameEvent event) { return getEntities("Entity", event.getEntity(), "Owner", event.getOwner()); }
    public HashMap<String, Entity> getEntities(FoodLevelChangeEvent event) { return getEntities("Player", event.getEntity()); }
    public HashMap<String, Entity> getEntities(PlayerEvent event) { return getEntities("Player", event.getPlayer()); }
    public HashMap<String, Entity> getEntities(PlayerFishEvent event) { return getEntities("Player", event.getPlayer(), "Caught", event.getCaught()); }
    public HashMap<String, Entity> getEntities(ProjectileHitEvent event) {
        final Projectile p = event.getEntity();
        return getEntities("Projectile", p, "Shooter", p.getShooter(), "Victim", getHitEntity(event));
    }
    public HashMap<String, Entity> getEntities(ProjectileLaunchEvent event) {
        final Projectile p = event.getEntity();
        return getEntities("Projectile", p, "Shooter", p.getShooter());
    }
    // RandomPackage event entities
    public HashMap<String, Entity> getEntities(CoinFlipEndEvent event) { return getEntities("Winner", event.winner, "Loser", event.loser); }
    public HashMap<String, Entity> getEntities(DamageEvent event) { return getEntities("Damager", event.getDamager(), "Victim", event.getEntity()); }
    public HashMap<String, Entity> getEntities(FallenHeroSlainEvent event) { return getEntities("Victim", event.hero.getEntity(), "Killer", event.killer); }
    public HashMap<String, Entity> getEntities(MobStackDepleteEvent event) { return getEntities("Killer", event.killer, "Victim", event.stack.entity); }
    public HashMap<String, Entity> getEntities(RPEvent event) { return getEntities("Player", event.getPlayer()); }

    private String[] getReplacements(Event event) {
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
            case "playerinteract": return getReplacements((PlayerInteractEvent) event);
            case "projectilehit": return getReplacements((ProjectileHitEvent) event);
            case "projectilelaunch": return getReplacements((ProjectileLaunchEvent) event);

            case "boostertrigger": return getReplacements((BoosterTriggerEvent) event);
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

            default: return new String[]{};
        }
    }

    // Bukkit event replacements
    public String[] getReplacements(EntityDeathEvent event) {
        final LivingEntity e = event.getEntity(), k = e.getKiller();
        final boolean NN = k != null;
        final String[] a = new String[] {"xp", Integer.toString(event.getDroppedExp()), "@Victim", toString(e.getLocation())}, b = NN ? new String[]{"@Killer", toString(k.getLocation())} : null;
        return NN ? getReplacements(a, b) : a;
    }
    public String[] getLocationReplacements(Entity entity, String id) {
        final String[] a = new String[]{"@" + id, toString(entity.getLocation())};
        return entity instanceof LivingEntity ?  getReplacements(a, new String[]{"@" + id + "EyeLocation", toString(((LivingEntity) entity).getEyeLocation())}) : a;
    }

    private String[] getProjectileReplacements(Projectile p) { return getReplacements(getLocationReplacements(p, "Projectile"), getLocationReplacements((Entity) p.getShooter(), "Shooter")); }

    public String[] getReplacements(BlockBreakEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"xp", Integer.toString(event.getExpToDrop()), "@Block", toString(event.getBlock().getLocation())}); }
    public String[] getReplacements(BlockGrowEvent event) { return new String[] {"@Block", toString(event.getBlock().getLocation())}; }
    public String[] getReplacements(BlockPlaceEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), (String[]) null); }
    public String[] getReplacements(PlayerFishEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"xp", Integer.toString(event.getExpToDrop()), "@Caught", toString(event.getCaught().getLocation())}); }
    public String[] getReplacements(EntityDamageEvent event) { return getReplacements(getLocationReplacements(event.getEntity(), "Victim"), new String[] {"dmg", Double.toString(event.getDamage())}); }
    public String[] getReplacements(EntityDamageByEntityEvent event) { return new String[] {"dmg", Double.toString(event.getDamage()), "@Damager", toString(event.getDamager().getLocation()), "@Victim", toString(event.getEntity().getLocation())}; }
    public String[] getReplacements(EntityShootBowEvent event) { return new String[] {"@Shooter", toString(event.getEntity().getLocation()), "@Projectile", toString(event.getProjectile().getLocation())}; }
    public String[] getReplacements(EntityTameEvent event) { return getReplacements(getLocationReplacements(event.getEntity(), "Entity"), (String[]) null); }
    public String[] getReplacements(FoodLevelChangeEvent event) {
        final ItemStack is = event.getItem();
        final String m = is != null ? is.getType().name() : "AIR";
        return getReplacements(getLocationReplacements(event.getEntity(), "Player"), new String[] {"{ITEM}", UMaterial.match(m).name()});
    }
    public String[] getReplacements(PlayerInteractEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), (String[]) null); }
    public String[] getReplacements(ProjectileHitEvent event) { return getProjectileReplacements(event.getEntity()); }
    public String[] getReplacements(ProjectileLaunchEvent event) { return getProjectileReplacements(event.getEntity()); }
    // RandomPackage event replacements
    public String[] getReplacements(BoosterTriggerEvent event) {
        final ActiveBooster a = event.booster;
        return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"multiplier", Double.toString(a.getMultiplier()), "duration", Long.toString(a.getDuration())});
    }
    public String[] getReplacements(CustomEnchantProcEvent event) {
        final HashMap<String, Entity> e = event.getEntities();
        final String[] a = getReplacements(event.getEvent()), b = new String[] {"@Player", toString(e.get("Player").getLocation()), "level", Integer.toString(event.getEnchantLevel()), "{ENCHANT}", event.getEnchant().getName()};
        return getReplacements(a, b);
    }
    public String[] getReplacements(DamageEvent event) {
        final String[] a = new String[]{"dmg", Double.toString(event.getDamage())};
        final List<String> b = new ArrayList<>();
        final Entity damager = event.getDamager();
        if(damager != null) {
            b.add("@Damager");
            b.add(toString(damager.getLocation()));
        }
        return getReplacements(a, b);
    }
    public String[] getReplacements(FundDepositEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"amount", event.amount.toString()}); }
    public String[] getReplacements(JackpotPurchaseTicketsEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"amount", event.amount.toBigInteger().toString()}); }
    public String[] getReplacements(KitClaimEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"level", Integer.toString(event.getLevel())}); }
    public String[] getReplacements(KitPreClaimEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"chance", Integer.toString(event.getLevelupChance()), "level", Integer.toString(event.getLevel())}); }
    public String[] getReplacements(LootbagClaimEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"size", Integer.toString(event.getRewardSize())}); }
    public String[] getReplacements(PlayerTeleportDelayEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"delay", Double.toString(event.getDelay())}); }
    public String[] getReplacements(ShopEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"total", event.getTotal().toString()}); }
    public String[] getReplacements(TinkererTradeEvent event) { return getReplacements(getLocationReplacements(event.getPlayer(), "Player"), new String[] {"tradesize", Integer.toString(event.trades.size())}); }

    public String[] getReplacements(String[] a, List<String> b) {
        final List<String> c = new ArrayList<>();
        c.addAll(Arrays.asList(a));
        c.addAll(b);
        return c.toArray(new String[a.length+b.size()]);
    }
    public String[] getReplacements(String[] a, String[] b) {
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
    /*
        CustomEnchant
     */
    private List<String> replaceCE(int level, List<String> attributes) {
        final String lvl = Integer.toString(level);
        final List<String> a = new ArrayList<>();
        for(String s : attributes) {
            a.add(s.replace("level", lvl));
        }
        return a;
    }

    private Player getSource(Event event) {
        switch (event.getEventName().toLowerCase().split("=")[0]) {
            case "pvany": return ((PvAnyEvent) event).getDamager();
            case "isdamaged": return ((isDamagedEvent) event).getEntity();
            // TODO: fix dis bruh
            default: return event instanceof RPEvent ? ((RPEvent) event).getPlayer() : null;
        }
    }
    public void triggerCustomEnchants(Event event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        triggerCustomEnchants(event, getEntities(event), enchants);
    }
    public void triggerCustomEnchants(Event event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        final Player player = entities.containsKey("Player") ? (Player) entities.get("Player") : getSource(event);
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                if(enchant.isOnCorrectItem(is) && enchant.canBeTriggered(player, is)) {
                    final int lvl = e.get(enchant);
                    final String[] replacements = new String[] {"level", Integer.toString(lvl), "{ENCHANT}", enchant.getName() + " " + toRoman(lvl)};
                    trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()), getReplacements(getReplacements(event), replacements));
                }
            }
        }
    }
    /*
        Other plugins
     */
    public boolean triggerCustomEnchants(McMMOPlayerAbilityActivateEvent event, List<String> attributes) { // TODO: fix this if they don't have mcmmo installed
        return trigger(event, getEntities("Player", event.getPlayer()), attributes);
    }
    public boolean triggerCustomEnchants(McMMOPlayerXpGainEvent event, List<String> attributes, String...replacements) { // TODO: fix this if they don't have mcmmo installed
        return trigger(event, getEntities("Player", event.getPlayer()), attributes, replacements);
    }
}
