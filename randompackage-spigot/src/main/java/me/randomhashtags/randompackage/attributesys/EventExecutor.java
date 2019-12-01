package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.EventAttribute;
import me.randomhashtags.randompackage.event.PvAnyEvent;
import me.randomhashtags.randompackage.event.RPEvent;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.util.obj.TObject;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
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

import static me.randomhashtags.randompackage.api.CustomEnchants.getCustomEnchants;

public abstract class EventExecutor extends RPFeature implements EventReplacements, EventReplacer {
    public boolean didPassConditions(Event event, HashMap<String, Entity> entities, List<String> conditions, HashMap<String, String> valueReplacements, boolean cancelled) {
        final String eventName = event.getEventName().toLowerCase().split("event")[0];
        final Player involved;
        switchloop: switch (eventName) {
            case "pvany":
                involved = ((PvAnyEvent) event).getDamager();
                break;
            case "isdamaged":
                involved = ((isDamagedEvent) event).getEntity();
                break;
            default:
                involved = null;
                break;
        }
        boolean passed = true, hasCancelled = false, didProc = false;

        final boolean isInteract = event instanceof PlayerInteractEvent;

        outerloop: for(String c : conditions) {
            final String condition = c.toLowerCase();
            final Set<String> keys = entities.keySet();
            for(String key : keys) {
                final String entityKey = key;
                final Entity e = entities.get(entityKey);
                String value = c.contains("=") ? c.split("=")[1] : "false";
                if(!value.replaceAll("\\p{L}", "").equals(value)) {
                    // contains an enchantment proc value
                    // TODO: allow more than 1 enchant in value
                    final String string = value.replaceAll("\\p{Z}", "").replaceAll("\\p{S}", "").replaceAll("\\p{N}", "").replaceAll("\\p{P}", "").replaceAll("\\p{Z}", "").replaceAll("\\p{M}", "");
                    final CustomEnchant enchant = valueOfCustomEnchant(string);
                    if(enchant != null) {
                        forloop: for(String attribute : enchant.getAttributes()) {
                            if(attribute.split(";")[0].equalsIgnoreCase(eventName)) {
                                final String procValue = enchant.getEnchantProcValue();
                                int levels = 0;
                                if(involved != null) {
                                    final LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchant, Integer>> enchants = getCustomEnchants().getEnchants(involved);
                                    for(LinkedHashMap<CustomEnchant, Integer> enchantLevels : enchants.values()) {
                                        if(enchantLevels.containsKey(enchant)) {
                                            levels += enchantLevels.get(enchant);
                                        }
                                    }
                                }
                                value = value.replace(string, procValue.replace("level", Integer.toString(levels)));
                                break forloop;
                            }
                        }
                    }
                }
                key = key.toLowerCase();
                if(valueReplacements != null) {
                    for(String r : valueReplacements.keySet()) {
                        final String replacement = valueReplacements.get(r);
                        if(replacement != null) {
                            key = key.replace(r.toLowerCase(), replacement);
                        }
                    }
                    value = replaceValue(entities, value, valueReplacements);
                }
                if(!hasCancelled && cancelled && !isInteract) {
                    passed = false;
                } else if(condition.startsWith("cancelled=")) {
                    passed = cancelled == Boolean.parseBoolean(value);
                    hasCancelled = true;
                } else if(condition.startsWith("chance=")) {
                    final boolean check = random.nextInt(100) < evaluate(value);
                    passed = check;
                    didProc = check;
                } else if(condition.startsWith("didproc")) {
                    passed = didProc;
                } else {
                    passed = passedAllConditions(event, entities, entityKey, e, condition, key, value);
                }
                if(!passed) {
                    break outerloop;
                }
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
        final HashMap<String, Entity> nearby = getNearbyEntities(player.getLocation(), radiusX, radiusY, radiusZ);
        if(type.equals("ENTITIES")) return nearby;
        final UUID u = player.getUniqueId();
        final boolean enemy = type.equals("ENEMY");
        final List<UUID> t = type.equals("ALLY") ? regions.getAllies(u) : enemy ? regions.getEnemies(u) : type.equals("TRUCE") ? regions.getTruces(u) : regions.getAssociates(u);
        final HashMap<String, Entity> n = new HashMap<>();
        for(String s : nearby.keySet()) {
            final Entity entity = nearby.get(s);
            if(entity instanceof Player && t.contains(entity.getUniqueId()) || enemy && !(entity instanceof Player)) {
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
                                final int ticks = (int) evaluate(replaceValue(entities, defaultValue, valueReplacements));
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
                                if(dadda) {
                                    a.executeData(entities, data, valueReplacements);
                                    a.executeData(data, valueReplacements);
                                }
                                valuez.remove(null);
                                for(Entity e : valuez.keySet()) {
                                    final String og = valuez.get(e);
                                    if(og != null) {
                                        valuez.put(e, replaceValue(entities, og, valueReplacements));
                                    }
                                }
                                a.execute(event, valuez);
                                a.execute(event, entities, valuez);
                                a.execute(event, entities, valuez, valueReplacements);
                                if(defaultValue != null) {
                                    a.execute(event, defaultValue);
                                    a.execute(event, entities, defaultValue, valueReplacements);
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

    private TObject replaceAllNearbyUsages(String string, List<String> entityKeys, HashMap<String, Entity> entities, String value1, boolean useStringAsVault) {
        final String og = string;
        string = string.toUpperCase();
        final HashMap<String, String> entityValues = new HashMap<>();
        boolean did = false;
        for(String entity : entityKeys) {
            final String E = entity.toUpperCase();
            if(string.contains("NEARBY" + E)) {
                final String nearbyString = "NEARBY" + E + string.split("NEARBY" + E)[1].split("\\)")[0], radius = nearbyString.split("\\(")[1];
                final boolean ally = string.contains(E + "ALLIES"), enemy = string.contains(E + "ENEMIES"), truce = string.contains(E + "TRUCES"), member = string.contains(E + "MEMBERS");
                if(ally || enemy || truce || member) {
                    final String type = ally ? "ALLIES" : enemy ? "ENEMIES" : truce ? "TRUCES" : "MEMBERS";
                    final boolean isSize = string.contains("SIZE");
                    final Entity en = entities.get(entity);
                    entities.remove(entity);
                    HashMap<String, Entity> nearby = null;
                    if(en instanceof Player) {
                        final Player player = (Player) en;
                        final String[] a = radius.split(":");
                        final int length = a.length;
                        final double x = evaluate(a[0]), y = length >= 2 ? evaluate(a[1]) : x, z = length >= 3 ? evaluate(a[2]) : x;
                        nearby = getNearbyType(player, x, y, z, ally ? "ALLY" : enemy ? "ENEMY" : truce ? "TRUCE" : member ? "MEMBER" : "ENTITIES");
                        entities.putAll(nearby);
                        for(String n : nearby.keySet()) {
                            entityValues.put(n, useStringAsVault ? string : value1);
                        }
                    }
                    string = string.replace("NEARBY" + E + type + (isSize ? "SIZE" : "") + "(" + radius + ")", isSize && nearby != null ? Integer.toString(nearby.size()) : "");
                }
                did = true;
            } else if(string.contains(E)) {
                string = string.replace(E, "");
                entityValues.put(entity, value1);
                did = true;
            }
        }
        return new TObject(did ? string : og, entityValues, entities);
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
                    final String[] attributez = s.split(semi[0] + ";");

                    if(attributez.length > 1) {
                        for(String c : attributez[1].split(";")) {
                            if(c.contains("=")) {
                                final String[] values = c.split("=");
                                final String v1 = values[1];
                                final TObject keyObj = replaceAllNearbyUsages(values[0], entityKeys, entities, v1, false), valueObj = replaceAllNearbyUsages(v1, entityKeys, entities, v1, true);
                                final String key = (String) keyObj.getFirst(), value = (String) valueObj.getFirst();
                                final HashMap<String, String> map = (HashMap<String, String>) keyObj.getSecond();
                                final HashMap<String, Entity> entitiesMap = (HashMap<String, Entity>) keyObj.getThird();
                                entityValues.putAll(map);
                                entities.putAll(entitiesMap);
                                final EventAttribute a = getEventAttribute(key);
                                if(a == null) {
                                    conditions.add(c);
                                } else {
                                    final LinkedHashMap<EventAttribute, HashMap<Entity, String>> attribute = new LinkedHashMap<>();
                                    attribute.put(a, new LinkedHashMap<>());
                                    final HashMap<Entity, String> attributeEntities = attribute.get(a);
                                    for(Entity entity : entities.values()) {
                                        attributeEntities.put(entity, value);
                                    }
                                    attributeEntities.put(null, value);
                                    execute.add(attribute);
                                }
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
            case "isdamaged":
                Entity e = ((isDamagedEvent) event).getDamager();
                return e instanceof Player ? (Player) e : null;

            case "entityshootbow":
                e = ((EntityShootBowEvent) event).getEntity();
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
        //final Player player = entities.containsKey("Player") ? (Player) entities.get("Player") : getSource(event);
        for(ItemStack is : enchants.keySet()) {
            final LinkedHashMap<CustomEnchant, Integer> e = enchants.get(is);
            for(CustomEnchant enchant : e.keySet()) {
                final boolean onCorrectItem = enchant.isOnCorrectItem(is);
                //final boolean canBeTriggered = enchant.canBeTriggered(event, player, is);
                //Bukkit.broadcastMessage("EventExecutor;enchant=" + enchant.getName() + ";onCorrectItem=" + onCorrectItem);
                if(onCorrectItem) {
                    final int lvl = e.get(enchant);
                    final String[] replacements = new String[] {"level", Integer.toString(lvl), "{ENCHANT}", enchant.getName() + " " + toRoman(lvl)}, replacementz = getReplacements(getReplacements(event), replacements);
                    try {
                        trigger(event, entities, replaceCE(lvl, globalattributes), replacementz);
                    } catch (Exception error) {
                        sendConsoleMessage("&6[RandomPackage] &cERROR &eCustom Enchant with identifier &f" + enchant.getIdentifier() + " &egenerated a global attribute error!");
                        error.printStackTrace();
                    }

                    try {
                        trigger(event, entities, replaceCE(lvl, enchant.getAttributes()), replacementz);
                    } catch (Exception error) {
                        sendConsoleMessage("&6[RandomPackage] &cERROR &eCustom Enchant with identifier &f" + enchant.getIdentifier() + " &egenerated an attribute error!");
                        error.printStackTrace();
                    }
                }
            }
        }
    }
}
