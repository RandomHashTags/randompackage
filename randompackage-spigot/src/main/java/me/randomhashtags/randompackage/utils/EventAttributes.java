package me.randomhashtags.randompackage.utils;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.EventAttribute;
import me.randomhashtags.randompackage.addons.EventCondition;
import me.randomhashtags.randompackage.attributes.*;
import me.randomhashtags.randompackage.attributes.conditions.*;
import me.randomhashtags.randompackage.events.*;
import me.randomhashtags.randompackage.events.armor.ArmorEvent;
import me.randomhashtags.randompackage.events.customenchant.*;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class EventAttributes extends Conditions {
    /*
        Read https://gitlab.com/RandomHashTags/randompackage-multi/wikis/Event-Attributes for all event attribute info
            * Event specific entity placeholders
            * Allowed conditions for specific entity types
            * Available event attributes with their identifier, and what they do
     */
    // TODO: Support individual event conditions
    protected static HashMap<UUID, EntityShootBowEvent> projectileEvents = new HashMap<>();

    static {
        if(eventattributes == null) {
            eventattributes = new LinkedHashMap<>();
        }
        final List<EventAttribute> attributes = Arrays.asList(
                // event attributes
                new SetDamage(),
                // attributes
                new AddPotionEffect(),
                new BreakHitBlock(),
                new ComboAdd(),
                new ComboDeplete(),
                new ComboStop(),
                new Damage(),
                new DepleteRarityGem(),
                new DropItem(),
                new ExecuteCommand(),
                //new Explode(),
                new Freeze(),
                new GiveDrops(),
                new GiveItem(),
                new Heal(),
                new Ignite(),
                new KickWithReason(),
                new PerformCommand(),
                new PlaySound(),
                new RemovePotionEffect(),
                new SendMessage(),
                new SetAir(),
                new SetCancelled(),
                new SetCompassTarget(),
                new SetDroppedExp(),
                new SetDurability(),
                new SetFlySpeed(),
                new SetGameMode(),
                new SetHealth(),
                new SetHunger(),
                new SetNoDamageTicks(),
                new SetSneaking(),
                new SetSprinting(),
                new SetWalkSpeed(),
                new SetXp(),
                new Smite(),
                new StealXp(),
                new Wait()
        );
        for(EventAttribute e : attributes) e.load();
        final List<EventCondition> conditions = Arrays.asList(
                new HasCombo(),
                new HasCustomEnchantEquipped(),
                new HitBlock(),
                new HitCEEntity(),
                new IsHeadshot()
        );
        for(EventCondition c : conditions) c.load();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void creatureSpawnEvent(CreatureSpawnEvent event) {
        if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            final UUID u = event.getEntity().getUniqueId();
            if(!spawnedFromSpawner.contains(u)) {
                spawnedFromSpawner.add(u);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
        spawnedFromSpawner.remove(event.getEntity().getUniqueId());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void entityShootBowEvent(EntityShootBowEvent event) {
        projectileEvents.put(event.getProjectile().getUniqueId(), event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void projectileHitEvent(ProjectileHitEvent event) {
        projectileEvents.remove(event.getEntity().getUniqueId());
    }

    private boolean hasReplacements(List<String> conditions) {
        for(String s : conditions) {
            final String l = s.toLowerCase();
            if(l.startsWith("get") && (l.contains("hp") || l.endsWith("saturation") || l.contains("exp") || l.contains("loc"))) {
                return true;
            }
        }
        return false;
    }
    private void doReplacements(HashMap<String, Entity> entities, Set<String> keys, String s) {
        // TODO: add more replacements
        for(String entity : keys) {
            final Entity E = entities.get(entity);
            final Location l = E.getLocation();
            final boolean isLiving = E instanceof LivingEntity, isPlayer = isLiving && E instanceof Player;
            final LivingEntity le = isLiving ? (LivingEntity) E : null;
            final Player player = isPlayer ? (Player) E : null;
            s = s.replace("get" + entity + "maxhp", isLiving ? Double.toString(le.getHealth()) : "0");
            s = s.replace("get" + entity + "hp", isLiving ? Double.toString(le.getHealth()) : "0");
            s = s.replace("get" + entity + "saturation", isPlayer ? Float.toString(player.getSaturation()) : "0");
            if(s.contains("loc")) {
                s = s.replace("get" + entity + "locx", Double.toString(l.getX()));
                s = s.replace("get" + entity + "locy", Double.toString(l.getY()));
                s = s.replace("get" + entity + "locz", Double.toString(l.getZ()));
            }
            if(s.contains("exp")) {
                s = s.replace("get" + entity + "exp", isPlayer ? Integer.toString(getTotalExperience(player)) : "0");
                s = s.replace("get" + entity + "explevel", isPlayer ? Integer.toString(player.getLevel()) : "0");
            }
        }
    }

    public boolean didPassConditions(Event event, HashMap<String, Entity> entities, List<String> conditions, boolean cancelled) {
        final boolean eight = version.contains("1.8"), nine = version.contains("1.9"), ten = version.contains("1.10"), eleven = version.contains("1.11"), thirteen = version.contains("1.13"), legacy = isLegacy;
        boolean passed = true;

        for(String c : conditions) {
            final String condition = c.toLowerCase();
            if(passed) {
                final Set<String> keys = entities.keySet();
                for(String s : keys) {
                    final String value = condition.contains("=") ? condition.split("=")[1] : "false";
                    final Entity e = entities.get(s);
                    s = s.toLowerCase();

                    if(hasReplacements(conditions)) {
                        doReplacements(entities, keys, s);
                    }

                    if(s.startsWith("cancelled=")) {
                        passed = cancelled == Boolean.parseBoolean(value);
                    } else if(s.startsWith("chance=")) {
                        passed = random.nextInt(100) < evaluate(value);
                    } else if(condition.startsWith(s)) {
                        passed = passedAllConditions(event, e, condition, s, value, legacy, eight, nine, ten, eleven, thirteen);
                    }
                    if(!passed) break;
                }
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

    protected HashMap<String, String> getReplacements(String...replacements) { return getReplacements(null, replacements); }
    protected HashMap<String, String> getReplacements(List<String> addedReplacements, String...replacements) {
        final HashMap<String, String> r = new HashMap<>();
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
    public boolean executeAll(Event event, HashMap<String, Entity> entities, List<String> conditions, boolean cancelled, HashMap<String, String> entityValues, List<LinkedHashMap<EventAttribute, HashMap<Entity, String>>> values, HashMap<Entity, HashMap<String, String>> valueReplacements) {
        final boolean passed = didPassConditions(event, entities, conditions, cancelled);
        if(passed) {
            final Entity entity1 = entities.getOrDefault("Player", entities.getOrDefault("Killer", entities.getOrDefault("Damager", entities.getOrDefault("Owner", null))));
            final Entity entity2 = entities.getOrDefault("Victim", entities.getOrDefault("Entity", null));
            final HashMap<RPPlayer, String> data = getData(entities, entityValues);
            final boolean dadda = !data.isEmpty(), entity1NN = entity1 != null, entity2NN = entity2 != null;
            for(LinkedHashMap<EventAttribute, HashMap<Entity, String>> hashmap : values) {
                for(EventAttribute a : hashmap.keySet()) {
                    if(!a.isCancelled()) {
                        final HashMap<Entity, String> valuez = hashmap.get(a);
                        final String defaultValue = valuez.getOrDefault(null, null);
                        if(a.getIdentifier().equals("WAIT")) {
                            final int ticks = (int) evaluate(defaultValue);
                            final List<LinkedHashMap<EventAttribute, HashMap<Entity, String>>> attributes = new ArrayList<>(values);
                            attributes.remove(hashmap);
                            scheduler.scheduleSyncDelayedTask(randompackage, () -> executeAll(event, entities, conditions, cancelled, entityValues, attributes, valueReplacements), ticks);
                            break;
                        } else {
                            a.execute(event);
                            if(dadda) a.executeData(data);
                            valuez.remove(null);
                            a.execute(event, valuez);
                            a.execute(event, valuez, valueReplacements);
                            if(defaultValue != null) {
                                a.execute(event, defaultValue);
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

    private boolean tryGeneric(Event event, HashMap<String, Entity> entities, List<String> attributes, HashMap<String, String> replacements) {
        return tryGeneric(event, entities, attributes, replacements, new HashMap<>());
    }
    private boolean tryGeneric(Event event, HashMap<String, Entity> entities, List<String> attributes, HashMap<String, String> replacements, HashMap<Entity, HashMap<String, String>> valueReplacements) {
        if(event != null && attributes != null && !attributes.isEmpty()) {
            final List<Boolean> checks = new ArrayList<>();
            final String e = event.getEventName().split("Event")[0].toLowerCase();
            final boolean cancellable = event instanceof Cancellable, cancelled = cancellable && ((Cancellable) event).isCancelled(), hasReplacements = replacements != null && !replacements.isEmpty();
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
                            if(hasReplacements) {
                                for(String r : replacements.keySet()) {
                                    value1 = value1.replace(r, replacements.get(r));
                                }
                            }
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
    public boolean trigger(Event event, HashMap<String, Entity> entities, List<String> attributes) {
        return trigger(event, entities, attributes, null, null);
    }
    public boolean trigger(Event event, HashMap<String, Entity> entities, List<String> attributes, HashMap<String, String> replacements) {
        return trigger(event, entities, attributes, replacements, new HashMap<>());
    }
    public boolean trigger(Event event, HashMap<String, Entity> entities, List<String> attributes, HashMap<String, String> replacements, HashMap<Entity, HashMap<String, String>> valueReplacements) {
        if(didPass(event, attributes) && entities != null) {
            return tryGeneric(event, entities, attributes, replacements, valueReplacements == null ? new HashMap<>() : valueReplacements);
        }
        return false;
    }
    /*
        Bukkit Events
     */
    public boolean trigger(EntityDeathEvent event, List<String> attributes, String...replacements) {
        if(didPass(event, attributes)) {
            final LivingEntity e = event.getEntity();
            final Player k = e.getKiller();
            if(k != null) {
                return tryGeneric(event, getEntities("Victim", e, "Killer", k), attributes, getReplacements(Arrays.asList("xp", Integer.toString(event.getDroppedExp())), replacements));
            }
        }
        return false;
    }
    public boolean trigger(BlockPlaceEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.getPlayer()), attributes, getReplacements(replacements));
    }
    public boolean trigger(BlockBreakEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.getPlayer()), attributes, getReplacements(Arrays.asList("xp", Integer.toString(event.getExpToDrop())), replacements));
    }
    public boolean trigger(EntityShootBowEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Projectile", event.getProjectile(), "Shooter", event.getEntity()), attributes, getReplacements(replacements));
    }
    public boolean trigger(ProjectileHitEvent event, List<String> attributes, String...replacements) {
        final Projectile p = event.getEntity();
        return trigger(event, getEntities("Projectile", p, "Shooter", p.getShooter(), "Victim", getHitEntity(event)), attributes, getReplacements(replacements));
    }
    public boolean trigger(EntityDamageEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Victim", event.getEntity()), attributes, getReplacements(replacements));
    }
    public boolean trigger(EntityDamageByEntityEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Damager", event.getDamager(), "Victim", event.getEntity()), attributes, getReplacements(replacements));
    }
    public boolean trigger(EntityTameEvent event, List<String> attributes) {
        return trigger(event, getEntities("Entity", event.getEntity(), "Owner", event.getOwner()), attributes);
    }
    public boolean trigger(FoodLevelChangeEvent event, List<String> attributes) {
        return trigger(event, getEntities("Player", event.getEntity()), attributes);
    }
    public boolean trigger(PlayerDeathEvent event, List<String> attributes) {
        final Player victim = event.getEntity(), killer = victim.getKiller();
        return trigger(event, getEntities("Player", victim, "Victim", victim, "Killer", killer), attributes);
    }
    public boolean trigger(PlayerExpChangeEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.getPlayer()), attributes, getReplacements(replacements));
    }
    public boolean trigger(PlayerFishEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.getPlayer(), "Caught", event.getCaught()), attributes, getReplacements(replacements));
    }
    public boolean trigger(PlayerInteractEvent event, List<String> attributes) {
        return trigger(event, getEntities("Player", event.getPlayer()), attributes);
    }
    public boolean trigger(PlayerItemDamageEvent event, List<String> attributes) {
        return trigger(event, getEntities("Player", event.getPlayer()), attributes);
    }
    public boolean trigger(PlayerJoinEvent event, List<String> attributes) {
        return trigger(event, getEntities("Player", event.getPlayer()), attributes);
    }
    /*
        RandomPackage Events
     */
    public boolean trigger(AlchemistExchangeEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(ArmorEvent event, List<String> attributes, String...replacements) {
        return trigger(event, attributes, null, replacements);
    }
    public boolean trigger(ArmorEvent event, List<String> attributes, HashMap<Entity, HashMap<String, String>> valueReplacements, String...replacements) {
        return trigger(event, getEntities("Player", event.getPlayer()), attributes, getReplacements(replacements), valueReplacements);
    }
    public boolean trigger(ArmorSetEquipEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(ArmorSetUnequipEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(CoinFlipEndEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Winner", event.winner, "Loser", event.loser), attributes, getReplacements(replacements));
    }
    public boolean trigger(CustomEnchantProcEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(DamageEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Damager", event.getDamager(), "Victim", event.getEntity()), attributes, getReplacements(Arrays.asList("dmg", Double.toString(event.getDamage())), replacements));
    }
    public boolean trigger(EnchanterPurchaseEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(FallenHeroSlainEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Victim", event.hero.getEntity(), "Killer", event.killer), attributes, getReplacements(replacements));
    }
    public boolean trigger(FundDepositEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(Arrays.asList("amount", event.amount.toString()), replacements));
    }
    public boolean trigger(CustomEnchantApplyEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(PlayerClaimEnvoyCrateEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(PlayerRevealCustomEnchantEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(RandomizationScrollUseEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(JackpotPurchaseTicketsEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(Arrays.asList("amount", event.amount.toBigInteger().toString()), replacements));
    }
    public boolean trigger(MaskEquipEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(MaskUnequipEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(MobStackDepleteEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Killer", event.killer, "Victim", event.stack.entity), attributes, getReplacements(replacements));
    }
    public boolean trigger(MysteryMobSpawnerOpenEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(ServerCrateOpenEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(ShopPurchaseEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(Arrays.asList("total", event.cost.toString()), replacements));
    }
    public boolean trigger(ShopSellEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(Arrays.asList("total", event.profit.toString()), replacements));
    }
    public boolean trigger(TinkererTradeEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(Arrays.asList("tradesize", Integer.toString(event.trades.size())), replacements));
    }
    /*
        CustomEnchant
     */
    private List<String> execute(int level, List<String> attributes) {
        final String lvl = Integer.toString(level);
        final List<String> a = new ArrayList<>();
        for(String s : attributes) {
            a.add(s.replace("level", lvl));
        }
        return a;
    }
    public void trigger(BlockBreakEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(BlockPlaceEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(DamageEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(EntityDamageEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(EntityDamageByEntityEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(EntityDeathEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(EntityShootBowEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(EntityTameEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(FoodLevelChangeEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(MobStackDepleteEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(ArmorEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        final Player player = event.getPlayer();
        final HashMap<Entity, HashMap<String, String>> valueReplacements = new HashMap<>();
        valueReplacements.put(player, new HashMap<>());
        final HashMap<String, String> r = valueReplacements.get(player);
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                final int lvl = e.get(enchant);
                r.put("level", Integer.toString(lvl));
                r.put("{ENCHANT}", enchant.getName() + " " + toRoman(lvl));
                trigger(event, enchant.getAttributes(), valueReplacements);
            }
        }
    }
    public void trigger(PlayerDeathEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(PlayerInteractEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(PlayerItemDamageEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(PlayerJoinEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }
    public void trigger(ProjectileHitEvent event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants) {
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                trigger(event, execute(e.get(enchant), enchant.getAttributes()));
            }
        }
    }

    /*
        Other plugins
     */
    public boolean trigger(McMMOPlayerAbilityActivateEvent event, List<String> attributes, String...replacements) { // TODO: fix this if they don't have mcmmo installed
        return trigger(event, getEntities("Player", event.getPlayer()), attributes, getReplacements(replacements));
    }
    public boolean trigger(McMMOPlayerXpGainEvent event, List<String> attributes, String...replacements) { // TODO: fix this if they don't have mcmmo installed
        return trigger(event, getEntities("Player", event.getPlayer()), attributes, getReplacements(replacements));
    }
}
