package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.EventAttribute;
import me.randomhashtags.randompackage.addon.util.EventReplacer;
import me.randomhashtags.randompackage.event.PvAnyEvent;
import me.randomhashtags.randompackage.event.RPEvent;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import me.randomhashtags.randompackage.util.RPPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public abstract class EventExecutor extends EventReplacements implements EventReplacer {
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
                final String entityKey = s;
                final Entity e = entities.get(entityKey);
                String value = c.contains("=") ? doReplacements(entities, keys, c.split("=")[1]) : "false";
                s = s.toLowerCase();
                if(valueReplacements != null) {
                    for(String r : valueReplacements.keySet()) {
                        s = s.replace(r.toLowerCase(), valueReplacements.get(r));
                    }
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
                    passed = (hasCancelled || !cancelled || isInteract) && passedAllConditions(event, entities, entityKey, e, condition, s, value, LEGACY, EIGHT, NINE, TEN, ELEVEN, THIRTEEN);
                }
                if(!passed) break outerloop;
            }
        }
        return passed;
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

    public boolean trigger(Event event, List<String> attributes, String...replacements) {
        final List<String> c = new ArrayList<>();
        final String[] a = getReplacements(event);
        c.addAll(Arrays.asList(replacements));
        c.addAll(Arrays.asList(a));
        final String[] d = c.toArray(new String[replacements.length+a.length]);
        return trigger(event, getEntities(event), attributes, d);
    }
    public boolean trigger(Event event, HashMap<String, Entity> entities, List<String> attributes, String...replacements) {
        if(event != null && attributes != null && !attributes.isEmpty() && entities != null) {
            return tryGeneric(event, entities, attributes, getReplacements(replacements));
        }
        return false;
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
        switch (event.getEventName().toLowerCase().split("event")[0]) {
            case "pvany": return ((PvAnyEvent) event).getDamager();
            case "isdamaged": return ((isDamagedEvent) event).getEntity();

            case "entityshootbow":
                final Entity e = ((EntityShootBowEvent) event).getEntity();
                return e instanceof Player ? (Player) e : null;
            case "projectilehit":
                final Projectile proj = ((ProjectileHitEvent) event).getEntity();
                final ProjectileSource shooter = proj.getShooter();
                return shooter instanceof Player ? (Player) shooter : null;
            // TODO: fix dis bruh
            default: return event instanceof RPEvent ? ((RPEvent) event).getPlayer() : null;
        }
    }
    public void triggerCustomEnchants(Event event, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants, List<String> globalattributes) {
        triggerCustomEnchants(event, getEntities(event), enchants, globalattributes);
    }
    public void triggerCustomEnchants(Event event, HashMap<String, Entity> entities, LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants, List<String> globalattributes) {
        final boolean doGlobal = globalattributes != null;
        final Player player = entities.containsKey("Player") ? (Player) entities.get("Player") : getSource(event);
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                if(enchant.isOnCorrectItem(is) && enchant.canBeTriggered(player, is)) {
                    final int lvl = e.get(enchant);
                    final String[] replacements = new String[] {"level", Integer.toString(lvl), "{ENCHANT}", enchant.getName() + " " + toRoman(lvl)}, replacementz = getReplacements(getReplacements(event), replacements);
                    final boolean passed = !doGlobal || trigger(event, entities, replaceCE(lvl, globalattributes), replacementz);
                    if(passed) trigger(event, entities, replaceCE(lvl, enchant.getAttributes()), replacementz);
                }
            }
        }
    }
}
