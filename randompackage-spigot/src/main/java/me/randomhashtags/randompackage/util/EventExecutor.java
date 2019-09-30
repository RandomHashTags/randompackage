package me.randomhashtags.randompackage.util;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.EventAttribute;
import me.randomhashtags.randompackage.addon.living.ActiveBooster;
import me.randomhashtags.randompackage.addon.util.EventReplacer;
import me.randomhashtags.randompackage.event.*;
import me.randomhashtags.randompackage.event.armor.ArmorEvent;
import me.randomhashtags.randompackage.event.booster.BoosterTriggerEvent;
import me.randomhashtags.randompackage.event.enchant.*;
import me.randomhashtags.randompackage.event.kit.KitClaimEvent;
import me.randomhashtags.randompackage.event.kit.KitPreClaimEvent;
import me.randomhashtags.randompackage.event.lootbag.LootbagClaimEvent;
import me.randomhashtags.randompackage.event.mob.FallenHeroSlainEvent;
import me.randomhashtags.randompackage.event.mob.MobStackDepleteEvent;
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
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class EventExecutor extends EventConditions implements EventReplacer {
    private boolean hasReplacements(List<String> conditions) {
        for(String s : conditions) {
            final String l = s.toLowerCase();
            if(l.startsWith("get") && (l.contains("combo") || l.contains("multiplier") || l.contains("hp") || l.endsWith("saturation") || l.contains("exp") || l.contains("loc"))) {
                return true;
            }
        }
        return false;
    }
    private void doReplacements(HashMap<String, Entity> entities, Set<String> keys, String s, String original) {
        // TODO: add more replacements
        for(String entity : keys) {
            final Entity E = entities.get(entity);
            final boolean isLiving = E instanceof LivingEntity, isPlayer = isLiving && E instanceof Player;
            final LivingEntity le = isLiving ? (LivingEntity) E : null;
            final Player player = isPlayer ? (Player) E : null;
            s = s.replace("get" + entity + "maxhp", isLiving ? Double.toString(le.getMaxHealth()) : "0");
            s = s.replace("get" + entity + "hp", isLiving ? Double.toString(le.getHealth()) : "0");
            s = s.replace("get" + entity + "saturation", isPlayer ? Float.toString(player.getSaturation()) : "0");
            if(s.contains("loc")) {
                final Location l = E.getLocation();
                s = s.replace("get" + entity + "locx", Double.toString(l.getX()));
                s = s.replace("get" + entity + "locy", Double.toString(l.getY()));
                s = s.replace("get" + entity + "locz", Double.toString(l.getZ()));
            }
            if(s.contains("exp")) {
                s = s.replace("get" + entity + "exp", isPlayer ? Integer.toString(getTotalExperience(player)) : "0");
                s = s.replace("get" + entity + "explevel", isPlayer ? Integer.toString(player.getLevel()) : "0");
            }
            final boolean hasCombo = s.contains("combo(");
            if(hasCombo || s.contains("multiplier(")) {
                final UUID u = E.getUniqueId();
                final String combo = original.split("\\(")[1].split("\\)")[0];
                s = s.replace("get" + entity + (hasCombo ? "combo" : "multiplier") + "(" + combo.toLowerCase() + ")", isPlayer ? Double.toString(getCombo(u, combo)) : "1");
            }
        }
    }

    public boolean didPassConditions(Event event, HashMap<String, Entity> entities, List<String> conditions, HashMap<String, String> valueReplacements, boolean cancelled) {
        boolean passed = true, hasCancelled = false;

        outerloop: for(String c : conditions) {
            final String condition = c.toLowerCase();
            final Set<String> keys = entities.keySet();
            for(String s : keys) {
                String value = c.contains("=") ? c.split("=")[1] : "false", original = s;
                final Entity e = entities.get(s);
                s = s.toLowerCase();
                for(String r : valueReplacements.keySet()) {
                    s = s.replace(r.toLowerCase(), valueReplacements.get(r));
                }
                if(hasReplacements(conditions)) {
                    doReplacements(entities, keys, s, original);
                }
                if(condition.startsWith("cancelled=")) {
                    passed = cancelled == Boolean.parseBoolean(value);
                    hasCancelled = true;
                } else if(condition.startsWith("chance=")) {
                    passed = random.nextInt(100) < evaluate(value);
                } else {
                    passed = (hasCancelled || !cancelled) && passedAllConditions(event, e, condition, s, value, LEGACY, EIGHT, NINE, TEN, ELEVEN, THIRTEEN);
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
        for(int i = 1; i <= nearby.size(); i++) {
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
            outerloop:
            for(LinkedHashMap<EventAttribute, HashMap<Entity, String>> hashmap : values) {
                for(EventAttribute a : hashmap.keySet()) {
                    if(!a.isCancelled()) {
                        final HashMap<Entity, String> valuez = hashmap.get(a);
                        String defaultValue = valuez.getOrDefault(null, null);
                        if(defaultValue != null) {
                            defaultValue = defaultValue.replace("RepeatID", repeatid);
                        }
                        final String id = a.getIdentifier();
                        if(id.equals("WAIT")) {
                            final int ticks = (int) evaluate(replaceValue(defaultValue, valueReplacements));
                            final List<LinkedHashMap<EventAttribute, HashMap<Entity, String>>> attributes = new ArrayList<>(values);
                            attributes.removeAll(previousHashMaps);
                            attributes.remove(hashmap);
                            scheduler.scheduleSyncDelayedTask(randompackage, () -> executeAll(event, entities, conditions, cancelled, entityValues, attributes, valueReplacements), ticks);
                            break outerloop;
                        } else if(id.equals("REPEAT")) {
                            final List<LinkedHashMap<EventAttribute, HashMap<Entity, String>>> attributes = new ArrayList<>(values);
                            attributes.removeAll(previousHashMaps);
                            attributes.remove(hashmap);
                            for(int i = 1; i <= evaluate(defaultValue); i++) {
                                executeAll(event, entities, conditions, cancelled, entityValues, attributes, valueReplacements, i);
                            }
                            break outerloop;
                        } else if(id.equals("RETURN")) {
                            passed = Boolean.parseBoolean(defaultValue);
                            if(!passed) break outerloop;
                        } else {
                            previousHashMaps.add(hashmap);
                            a.execute(event);
                            if(dadda) a.executeData(data, valueReplacements);
                            valuez.remove(null);
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
                            String value1 = values[1], string = values[0].toUpperCase();
                            for(String entity : entities.keySet()) {
                                final String E = entity.toUpperCase();
                                if(string.startsWith("NEARBY" + E)) {
                                    final String radius = fvalues[1].split("\\)")[0];
                                    final boolean ally = string.contains(E + "ALLIES"), enemy = string.contains(E + "ENEMIES"), truce = string.contains(E + "TRUCES"), member = string.contains(E + "MEMBERS");
                                    if(ally || enemy || truce || member) {
                                        string = string.replace("NEARBY" + E + (ally ? "ALLIES" : enemy ? "ENEMIES" : truce ? "TRUCES" : "MEMBERS") + "(" + radius + ")", "");
                                        final Entity en = entities.get(entity);
                                        if(en instanceof Player) {
                                            final Player player = (Player) en;
                                            final String[] a = radius.split(":");
                                            final double x = evaluate(a[0]), y = evaluate(a[1]), z = evaluate(a[2]);
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
    public boolean trigger(Event event, HashMap<String, Entity> entities, List<String> attributes, String...replacements) {
        if(didPass(event, attributes) && entities != null) {
            return tryGeneric(event, entities, attributes, getReplacements(replacements));
        }
        return false;
    }


    public HashMap<String, Entity> getEntities(Event event) {
        if(event instanceof BlockBreakEvent) return getEntities((BlockBreakEvent) event);
        else if(event instanceof BlockPlaceEvent) return getEntities((BlockPlaceEvent) event);
        else if(event instanceof EntityDeathEvent) return getEntities((EntityDeathEvent) event);
        else if(event instanceof EntityDamageByEntityEvent) return getEntities((EntityDamageByEntityEvent) event);
        else if(event instanceof EntityDamageEvent) return getEntities((EntityDamageEvent) event);
        else if(event instanceof EntityShootBowEvent) return getEntities((EntityShootBowEvent) event);
        else if(event instanceof EntityTameEvent) return getEntities((EntityTameEvent) event);
        else if(event instanceof FoodLevelChangeEvent) return getEntities((FoodLevelChangeEvent) event);
        else if(event instanceof PlayerFishEvent) return getEntities((PlayerFishEvent) event);
        else if(event instanceof PlayerEvent) return getEntities((PlayerEvent) event);
        else if(event instanceof ProjectileHitEvent) return getEntities((ProjectileHitEvent) event);
        return new HashMap<>();
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

    public HashMap<String, Entity> getEntities(DamageEvent event) { return getEntities("Damager", event.getDamager(), "Victim", event.getEntity()); }
    public HashMap<String, Entity> getEntities(RPEvent event) { return getEntities("Player", event.getPlayer()); }
    public HashMap<String, Entity> getEntities(ShopEvent event) { return getEntities("Player", event.getPlayer()); }

    private String[] getReplacements(Event event) {
        if(event instanceof EntityDeathEvent) return getReplacements((EntityDeathEvent) event);
        else if(event instanceof BlockGrowEvent) return getReplacements((BlockGrowEvent) event);
        else if(event instanceof BlockPlaceEvent) return getReplacements((BlockPlaceEvent) event);
        else if(event instanceof PlayerFishEvent) return getReplacements((PlayerFishEvent) event);
        else if(event instanceof EntityDamageByEntityEvent) return getReplacements((EntityDamageByEntityEvent) event);
        else if(event instanceof EntityDamageEvent) return getReplacements((EntityDamageEvent) event);
        else if(event instanceof EntityShootBowEvent) return getReplacements((EntityShootBowEvent) event);
        else if(event instanceof FoodLevelChangeEvent) return getReplacements((FoodLevelChangeEvent) event);
        else if(event instanceof PlayerInteractEvent) return getReplacements((PlayerInteractEvent) event);
        else if(event instanceof ProjectileHitEvent) return getReplacements((ProjectileHitEvent) event);

        else if(event instanceof BoosterTriggerEvent) return getReplacements((BoosterTriggerEvent) event);
        else if(event instanceof CustomEnchantProcEvent) return getReplacements((CustomEnchantProcEvent) event);
        else if(event instanceof DamageEvent) return getReplacements((DamageEvent) event);
        else if(event instanceof FundDepositEvent) return getReplacements((FundDepositEvent) event);
        else if(event instanceof JackpotPurchaseTicketsEvent) return getReplacements((JackpotPurchaseTicketsEvent) event);
        else if(event instanceof KitClaimEvent) return getReplacements((KitClaimEvent) event);
        else if(event instanceof KitPreClaimEvent) return getReplacements((KitPreClaimEvent) event);
        else if(event instanceof LootbagClaimEvent) return getReplacements((LootbagClaimEvent) event);
        else if(event instanceof PlayerTeleportDelayEvent) return getReplacements((PlayerTeleportDelayEvent) event);
        else if(event instanceof ShopEvent) return getReplacements((ShopEvent) event);
        else if(event instanceof TinkererTradeEvent) return getReplacements((TinkererTradeEvent) event);

        else return new String[]{};
    }
    // Bukkit event replacements
    public String[] getReplacements(EntityDeathEvent event) {
        final LivingEntity e = event.getEntity(), k = e.getKiller();
        final boolean NN = k != null;
        final String[] a = new String[] {"xp", Integer.toString(event.getDroppedExp()), "@Victim", toString(e.getLocation())}, b = NN ? new String[]{"@Killer", toString(k.getLocation())} : null;
        return NN ? getReplacements(a, b) : a;
    }
    public String[] getReplacements(BlockBreakEvent event) { return new String[] {"xp", Integer.toString(event.getExpToDrop()), "@Player", toString(event.getPlayer().getLocation()), "@Block", toString(event.getBlock().getLocation())}; }
    public String[] getReplacements(BlockGrowEvent event) { return new String[] {"@Block", toString(event.getBlock().getLocation())}; }
    public String[] getReplacements(BlockPlaceEvent event) { return new String[] {"@Player", toString(event.getPlayer().getLocation())}; }
    public String[] getReplacements(PlayerFishEvent event) { return new String[] {"xp", Integer.toString(event.getExpToDrop()), "@Player", toString(event.getPlayer().getLocation()), "@Caught", toString(event.getCaught().getLocation())}; }
    public String[] getReplacements(EntityDamageEvent event) { return new String[] {"dmg", Double.toString(event.getDamage()), "@Victim", toString(event.getEntity().getLocation())}; }
    public String[] getReplacements(EntityDamageByEntityEvent event) { return new String[] {"dmg", Double.toString(event.getDamage()), "@Damager", toString(event.getDamager().getLocation()), "@Victim", toString(event.getEntity().getLocation())}; }
    public String[] getReplacements(EntityShootBowEvent event) { return new String[] {"@Shooter", toString(event.getEntity().getLocation()), "@Projectile", toString(event.getProjectile().getLocation())}; }
    public String[] getReplacements(EntityTameEvent event) { return new String[] {"@Entity", toString(event.getEntity().getLocation())}; }
    public String[] getReplacements(FoodLevelChangeEvent event) {
        final ItemStack is = event.getItem();
        final String m = is != null ? is.getType().name() : "AIR";
        return new String[] {"@Player", toString(event.getEntity().getLocation()), "{ITEM}", UMaterial.match(m).name()};
    }
    public String[] getReplacements(PlayerInteractEvent event) { return new String[] {"@Player", toString(event.getPlayer().getLocation())}; }
    public String[] getReplacements(ProjectileHitEvent event) {
        final Projectile p = event.getEntity();
        return new String[] {"@Projectile", toString(p.getLocation()), "@Shooter", toString(((Entity) p.getShooter()).getLocation())};
    }
    // RandomPackage event replacements
    public String[] getReplacements(BoosterTriggerEvent event) {
        final ActiveBooster a = event.booster;
        return new String[] {"@Player", toString(event.getPlayer().getLocation()), "multiplier", Double.toString(a.getMultiplier()), "duration", Long.toString(a.getDuration())};
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
    public String[] getReplacements(FundDepositEvent event) { return new String[] { "@Player", toString(event.getPlayer().getLocation()), "amount", event.amount.toString()}; }
    public String[] getReplacements(JackpotPurchaseTicketsEvent event) { return new String[] { "@Player", toString(event.getPlayer().getLocation()), "amount", event.amount.toBigInteger().toString()}; }
    public String[] getReplacements(KitClaimEvent event) { return new String[] {"@Player", toString(event.getPlayer().getLocation()), "level", Integer.toString(event.getLevel())}; }
    public String[] getReplacements(KitPreClaimEvent event) { return new String[] {"@Player", toString(event.getPlayer().getLocation()), "chance", Integer.toString(event.getLevelupChance()), "level", Integer.toString(event.getLevel())}; }
    public String[] getReplacements(LootbagClaimEvent event) { return new String[]{"@Player", toString(event.getPlayer().getLocation()), "size", Integer.toString(event.getRewardSize())}; }
    public String[] getReplacements(PlayerTeleportDelayEvent event) {
        return new String[] { "@Player", toString(event.getPlayer().getLocation()), "delay", Double.toString(event.getDelay())};
    }
    public String[] getReplacements(ShopEvent event) { return new String[] { "@Player", toString(event.getPlayer().getLocation()), "total", event.getTotal().toString()}; }
    public String[] getReplacements(TinkererTradeEvent event) { return new String[] { "@Player", toString(event.getPlayer().getLocation()), "tradesize", Integer.toString(event.trades.size())}; }

    public String[] getReplacements(String[] a, List<String> b) {
        final List<String> c = new ArrayList<>();
        c.addAll(Arrays.asList(a));
        c.addAll(b);
        return c.toArray(new String[a.length+b.size()]);
    }
    public String[] getReplacements(String[] a, String[] b) {
        final List<String> c = new ArrayList<>();
        c.addAll(Arrays.asList(a));
        c.addAll(Arrays.asList(b));
        return c.toArray(new String[a.length+b.length]);
    }
    /*
        Bukkit Events
     */
    public boolean triggerBasic(Event event, List<String> attributes, String...replacements) { return trigger(event, getEntities(event), attributes, getReplacements(getReplacements(event), replacements)); }

    public boolean trigger(BlockBreakEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(BlockPlaceEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(EntityDamageEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(EntityDamageByEntityEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(EntityDeathEvent event, List<String> attributes, String...replacements) { return trigger(event, getEntities(event), attributes, getReplacements(getReplacements(event), replacements)); }
    public boolean trigger(EntityShootBowEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(FoodLevelChangeEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(PlayerFishEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(PlayerEvent event, List<String> attributes, String...replacements) { return trigger(event, getEntities(event), attributes, getReplacements(getReplacements(event), replacements)); }
    public boolean trigger(PlayerInteractEvent event, List<String> attributes, String...replacements) { return trigger(event, getEntities(event), attributes, getReplacements(getReplacements(event), replacements)); }
    public boolean trigger(ProjectileHitEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    /*
        RandomPackage Events
     */
    public boolean trigger(RPEvent event, List<String> attributes) { return trigger(event, getEntities("Player", event.getPlayer()), attributes); }
    public boolean trigger(BoosterTriggerEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(CoinFlipEndEvent event, List<String> attributes) { return trigger(event, getEntities("Winner", event.winner, "Loser", event.loser), attributes); }
    public boolean trigger(CustomEnchantProcEvent event, List<String> attributes) { return trigger(event, event.getEntities(), attributes, getReplacements(event)); }
    public boolean trigger(DamageEvent event, List<String> attributes, String...replacements) { return trigger(event, getEntities(event), attributes, getReplacements(getReplacements(event), replacements)); }
    public boolean trigger(FallenHeroSlainEvent event, List<String> attributes) { return trigger(event, getEntities("Victim", event.hero.getEntity(), "Killer", event.killer), attributes); }
    public boolean trigger(FundDepositEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(JackpotPurchaseTicketsEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(KitClaimEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(KitPreClaimEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(MobStackDepleteEvent event, List<String> attributes) { return trigger(event, getEntities("Killer", event.killer, "Victim", event.stack.entity), attributes); }
    public boolean trigger(PlayerTeleportDelayEvent event, List<String> attributes, String...replacements) { return trigger(event, getEntities(event), attributes, getReplacements(getReplacements(event), replacements)); }
    public boolean trigger(ShopEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
    public boolean trigger(TinkererTradeEvent event, List<String> attributes) { return trigger(event, getEntities(event), attributes, getReplacements(event)); }
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
    public void trigger(BlockBreakEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()), getReplacements(event));
            }
        }
    }
    public void trigger(BlockPlaceEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()), getReplacements(event));
            }
        }
    }
    public void trigger(DamageEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()), getReplacements(event));
            }
        }
    }
    public void trigger(EntityDamageEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()), getReplacements(event));
            }
        }
    }
    public void trigger(EntityDamageByEntityEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()), getReplacements(event));
            }
        }
    }
    public void trigger(EntityDeathEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()), getReplacements(event));
            }
        }
    }
    public void trigger(EntityShootBowEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()), getReplacements(event));
            }
        }
    }
    public void trigger(EntityTameEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()), getReplacements(event));
            }
        }
    }
    public void trigger(FoodLevelChangeEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()), getReplacements(event));
            }
        }
    }
    public void trigger(MobStackDepleteEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(ArmorEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                final int lvl = e.get(enchant);
                final String[] replacements = new String[] {"level", Integer.toString(lvl), "{ENCHANT}", enchant.getName() + " " + toRoman(lvl)};
                trigger(event, entities, enchant.getAttributes(), replacements);
            }
        }
    }
    public void trigger(PlayerEvent event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, entities, replaceCE(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(ProjectileHitEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, replaceCE(e.get(enchant), enchant.getAttributes()));
            }
        }
    }

    /*
        Other plugins
     */
    public boolean trigger(McMMOPlayerAbilityActivateEvent event, List<String> attributes) { // TODO: fix this if they don't have mcmmo installed
        return trigger(event, getEntities("Player", event.getPlayer()), attributes);
    }
    public boolean trigger(McMMOPlayerXpGainEvent event, List<String> attributes, String...replacements) { // TODO: fix this if they don't have mcmmo installed
        return trigger(event, getEntities("Player", event.getPlayer()), attributes, replacements);
    }
}
