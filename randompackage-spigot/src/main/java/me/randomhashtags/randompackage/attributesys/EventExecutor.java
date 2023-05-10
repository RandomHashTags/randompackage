package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.attribute.EventAttribute;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.RPPlayer;
import me.randomhashtags.randompackage.event.PvAnyEvent;
import me.randomhashtags.randompackage.event.RPEvent;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.obj.EquippedCustomEnchants;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public interface EventExecutor extends RPFeatureSpigot, EventReplacements, EventReplacer {
    default boolean didPassConditions(Event event, HashMap<String, Entity> entities, List<String> conditions, HashMap<String, String> valueReplacements, boolean cancelled) {
        final String event_name = event.getEventName().toLowerCase().split("event")[0];
        final Player involved;
        switch (event_name) {
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
        final CustomEnchants custom_enchants = CustomEnchants.INSTANCE;

        outerloop: for(String condition : conditions) {
            final String conditionLowercase = condition.toLowerCase();
            final Set<String> keys = entities.keySet();
            for(String key : keys) {
                final String entityKey = key;
                final Entity e = entities.get(entityKey);
                String value = condition.contains("=") ? condition.split("=")[1] : "false";
                if(!value.replaceAll("\\p{L}", "").equals(value)) {
                    // contains an enchantment proc value
                    // TODO: allow more than 1 enchant in value
                    final String string = value.replaceAll("\\p{Z}", "").replaceAll("\\p{S}", "").replaceAll("\\p{N}", "").replaceAll("\\p{P}", "").replaceAll("\\p{Z}", "").replaceAll("\\p{M}", "");
                    final CustomEnchantSpigot enchant = valueOfCustomEnchant(string);
                    if(enchant != null) {
                        forloop: for(String attribute : enchant.getAttributes()) {
                            if(attribute.split(";")[0].equalsIgnoreCase(event_name)) {
                                final String procValue = enchant.getEnchantProcValue();
                                int levels = 0;
                                if(involved != null) {
                                    final EquippedCustomEnchants equipped = custom_enchants.getEnchants(involved);
                                    for(LinkedHashMap<CustomEnchantSpigot, Integer> enchantLevels : equipped.getInfo().values()) {
                                        if(enchantLevels != null && enchantLevels.containsKey(enchant)) {
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
                } else if(conditionLowercase.startsWith("cancelled=")) {
                    passed = cancelled == Boolean.parseBoolean(value);
                    hasCancelled = true;
                } else if(conditionLowercase.startsWith("chance=")) {
                    final double chance = evaluate(value);
                    final boolean check = RANDOM.nextInt(100) < chance;
                    passed = check;
                    didProc = check;
                } else if(conditionLowercase.startsWith("didproc")) {
                    passed = didProc;
                } else {
                    passed = passedAllConditions(event, entities, entityKey, e, conditionLowercase, key, value);
                }
                if(!passed) {
                    break outerloop;
                }
            }
        }
        return passed;
    }

    default HashMap<String, Entity> getNearbyEntities(Location center, double radius) {
        return getNearbyEntities(center, radius, radius, radius);
    }
    default HashMap<String, Entity> getNearbyEntities(Location center, double radiusX, double radiusY, double radiusZ) {
        final HashMap<String, Entity> nearbyEntities = new HashMap<>();
        final List<Entity> nearby = new ArrayList<>(center.getWorld().getNearbyEntities(center, radiusX, radiusY, radiusZ));
        for(int i = 0; i < nearby.size(); i++) {
            nearbyEntities.put("Nearby" + i, nearby.get(i));
        }
        return nearbyEntities;
    }
    private HashMap<String, Entity> getNearbyType(Player player, double radiusX, double radiusY, double radiusZ, String type) {
        final HashMap<String, Entity> nearby = getNearbyEntities(player.getLocation(), radiusX, radiusY, radiusZ);
        if(type.equals("ENTITIES")) {
            return nearby;
        }
        final RegionalAPI regions = RegionalAPI.INSTANCE;
        final UUID uuid = player.getUniqueId();
        final boolean enemy = type.equals("ENEMY");
        final List<UUID> targetUUIDs = type.equals("ALLY") ? regions.getAllies(uuid) : enemy ? regions.getEnemies(uuid) : type.equals("TRUCE") ? regions.getTruces(uuid) : regions.getAssociates(uuid);
        final HashMap<String, Entity> nearbyEntities = new HashMap<>();
        for(String s : nearby.keySet()) {
            final Entity entity = nearby.get(s);
            if(entity instanceof Player && targetUUIDs.contains(entity.getUniqueId()) || enemy && !(entity instanceof Player)) {
                nearbyEntities.put(s, entity);
            }
        }
        return nearbyEntities;
    }

    private HashMap<RPPlayer, String> getData(HashMap<String, Entity> entities, HashMap<String, String> entityValues) {
        final HashMap<RPPlayer, String> a = new HashMap<>();
        for(String s : entities.keySet()) {
            if(entityValues.containsKey(s)) {
                final Entity e = entities.get(s);
                if(e instanceof Player) {
                    a.put(FileRPPlayer.get(e.getUniqueId()), entityValues.get(s));
                }
            }
        }
        return a;
    }

    default boolean executeAll(Event event, HashMap<String, Entity> entities, List<String> conditions, boolean cancelled, HashMap<String, String> entityValues, List<PendingEventAttribute> values) {
        return executeAll(event, entities, conditions, cancelled, entityValues, values, new HashMap<>());
    }
    default boolean executeAll(Event event, HashMap<String, Entity> entities, List<String> conditions, boolean cancelled, HashMap<String, String> entityValues, List<PendingEventAttribute> values, HashMap<String, String> valueReplacements) {
        return executeAll(event, entities, conditions, cancelled, entityValues, values, valueReplacements, 0);
    }
    default boolean executeAll(Event event, HashMap<String, Entity> entities, List<String> conditions, boolean cancelled, HashMap<String, String> entityValues, List<PendingEventAttribute> values, HashMap<String, String> valueReplacements, int repeatID) {
        boolean passed = didPassConditions(event, entities, conditions, valueReplacements, cancelled);
        if(passed) {
            final Entity entity1 = entities.getOrDefault("Player", entities.getOrDefault("Killer", entities.getOrDefault("Damager", entities.getOrDefault("Owner", null))));
            final Entity entity2 = entities.getOrDefault("Victim", entities.getOrDefault("Entity", null));
            final HashMap<RPPlayer, String> data = getData(entities, entityValues);
            final boolean dadda = !data.isEmpty(), entity1NN = entity1 != null, entity2NN = entity2 != null;
            final String repeatid = Integer.toString(repeatID);
            final List<PendingEventAttribute> previousHashMaps = new ArrayList<>();
            attributeLooper: for(PendingEventAttribute pending : values) {
                final EventAttribute attribute = pending.getEventAttribute();
                if(!attribute.isCancelled()) {
                    final HashMap<Entity, String> valuez = pending.getRecipientValues();
                    String defaultValue = valuez.getOrDefault(null, null);
                    if(defaultValue != null) {
                        defaultValue = defaultValue.replace("RepeatID", repeatid);
                    }
                    switch (attribute.getIdentifier()) {
                        case "WAIT":
                            final String target = replaceValue(entities, defaultValue, valueReplacements);
                            final int ticks = (int) evaluate(target);
                            List<PendingEventAttribute> attributes = new ArrayList<>(values);
                            attributes.removeAll(previousHashMaps);
                            attributes.remove(pending);
                            SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> executeAll(event, entities, conditions, cancelled, entityValues, attributes, valueReplacements), ticks);
                            break attributeLooper;
                        case "REPEAT":
                            attributes = new ArrayList<>(values);
                            attributes.removeAll(previousHashMaps);
                            attributes.remove(pending);
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
                            previousHashMaps.add(pending);
                            attribute.execute(pending);
                            if(dadda) {
                                attribute.executeData(entities, data, valueReplacements);
                                attribute.executeData(data, valueReplacements);
                            }
                            valuez.remove(null);
                            for(Entity e : valuez.keySet()) {
                                final String og = valuez.get(e);
                                if(og != null) {
                                    valuez.put(e, replaceValue(entities, og, valueReplacements));
                                }
                            }
                            attribute.execute(pending, valueReplacements);
                            if(defaultValue != null) {
                                attribute.execute(pending, defaultValue);
                                attribute.execute(pending, defaultValue, valueReplacements);
                                if(entity1NN && entity2NN) {
                                    attribute.execute(entity1, entity2, defaultValue);
                                }
                            }
                    }
                }
            }
        }
        return passed;
    }

    private LinkedHashMap<String, Entity> getEntitiesIn(String attributeKey, HashMap<String, Entity> entityList, List<String> entityKeys) {
        attributeKey = attributeKey.toUpperCase();
        final LinkedHashMap<String, Entity> entities = new LinkedHashMap<>();
        for(String entity : entityKeys) {
            final String targetEntity = entity.toUpperCase();
            if(attributeKey.contains("NEARBY" + targetEntity)) {
                attributeKey = replaceNearby(attributeKey, entity, entities);
            } else if(attributeKey.contains(targetEntity) && !attributeKey.contains(":" + targetEntity)) {
                attributeKey = attributeKey.replace(targetEntity, "");
                entities.put(entity, entityList.get(entity));
            }
        }
        return entities;
    }
    private TObject replaceAllNearbyUsages(String string, List<String> entityKeys, HashMap<String, Entity> entities, String value1, boolean useStringAsValue) {
        final String original = string;
        string = string.toUpperCase();
        final HashMap<String, String> entityValues = new HashMap<>();
        boolean did = false;
        for(String entity : entityKeys) {
            if(string.contains("NEARBY" + entity.toUpperCase())) {
                string = replaceNearby(entityValues, string, entity, entities, value1, useStringAsValue);
                did = true;
            } else if(original.contains(entity) && !original.contains(":" + entity) && !original.startsWith(entity + ":") && !original.startsWith("@" + entity + ":") && !original.contains("get" + entity)) {
                string = original.replace(entity, "").toUpperCase();
                entityValues.put(entity, value1);
                did = true;
            }
        }
        return new TObject(did ? string : original, entityValues, entities);
    }
    private String replaceNearby(String string, String entity, HashMap<String, Entity> entities) {
        return replaceNearby(null, string, entity, entities, null, false);
    }
    private String replaceNearby(HashMap<String, String> entityValues, String string, String entity, HashMap<String, Entity> entities, String value1, boolean useStringAsValue) {
        final String targetEntity = entity.toUpperCase();
        final String nearbyType = string.split("NEARBY" + targetEntity)[1].split("\\[")[0];
        final String radius = string.split("\\[")[1].split("]")[0];
        final boolean ally = nearbyType.equals("ALLIES"), enemy = nearbyType.equals("ENEMIES"), truce = nearbyType.equals("TRUCES"), member = nearbyType.equals("MEMBERS");
        if(ally || enemy || truce || member) {
            final String type = ally ? "ALLIES" : enemy ? "ENEMIES" : truce ? "TRUCES" : "MEMBERS";
            final boolean isSize = string.contains("SIZE");
            final Entity en = entities.get(entity);
            entities.remove(entity);
            HashMap<String, Entity> nearby = null;
            if(en instanceof Player) {
                final Player player = (Player) en;
                final String[] values = radius.split(":");
                final int length = values.length;
                final double x = evaluate(values[0]), y = length >= 2 ? evaluate(values[1]) : x, z = length >= 3 ? evaluate(values[2]) : x;
                nearby = getNearbyType(player, x, y, z, ally ? "ALLY" : enemy ? "ENEMY" : truce ? "TRUCE" : "MEMBER");
                entities.putAll(nearby);
                if(entityValues != null) {
                    for(String nearbyEntity : nearby.keySet()) {
                        entityValues.put(nearbyEntity, useStringAsValue ? string : value1);
                    }
                }
            }
            return string.replace("NEARBY" + targetEntity + type + (isSize ? "SIZE" : "") + "[" + radius + "]", isSize && nearby != null ? Integer.toString(nearby.size()) : "");
        }
        return string;
    }

    private boolean tryGeneric(Event event, HashMap<String, Entity> entities, List<String> attributes) {
        return tryGeneric(event, entities, attributes, new LinkedHashMap<>());
    }
    private boolean tryGeneric(Event event, HashMap<String, Entity> entities, List<String> attributes, HashMap<String, String> valueReplacements) {
        if(event != null && attributes != null && !attributes.isEmpty()) {
            final List<Boolean> checks = new ArrayList<>();
            final String targetEvent = event.getEventName().split("Event")[0].toLowerCase();
            final boolean cancelled = event instanceof Cancellable && ((Cancellable) event).isCancelled();
            final List<String> entityKeys = new ArrayList<>(entities.keySet());
            for(String s : attributes) {
                final String first = s.split(";")[0];
                if(first.toLowerCase().equals(targetEvent)) {
                    final List<String> conditions = new ArrayList<>();
                    final HashMap<String, String> entityValues = new HashMap<>();
                    final List<PendingEventAttribute> execute = new ArrayList<>();
                    final String[] attributeList = s.split(first + ";");

                    if(attributeList.length > 1) {
                        for(String string : attributeList[1].split(";")) {
                            if(string.contains("=")) {
                                final String[] values = string.split("=");
                                final String attributeKey = values[0], targetValue = values[1];
                                final TObject keyObj = replaceAllNearbyUsages(attributeKey, entityKeys, entities, targetValue, false), valueObj = replaceAllNearbyUsages(targetValue, entityKeys, entities, targetValue, true);
                                final String key = ((String) keyObj.getFirst()).toUpperCase(), value = (String) valueObj.getFirst();
                                final HashMap<String, String> map = (HashMap<String, String>) keyObj.getSecond();

                                entityValues.putAll(map);
                                final EventAttribute attribute = getEventAttribute(key);
                                if(attribute == null) {
                                    conditions.add(string);
                                } else {
                                    final HashMap<Entity, String> recipientValues = new HashMap<>();
                                    final HashMap<String, Entity> entitiesInKey = getEntitiesIn(attributeKey, entities, entityKeys);
                                    for(Entity entity : entitiesInKey.values()) {
                                        recipientValues.put(entity, value);
                                    }
                                    recipientValues.put(null, value);
                                    final PendingEventAttribute pending = new PendingEventAttribute(event, attribute, entities, entitiesInKey, (HashMap<String, Entity>) valueObj.getThird(), recipientValues, string);
                                    execute.add(pending);
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

    default boolean trigger(Event event, List<String> attributes, String...replacements) {
        final List<String> list = new ArrayList<>();
        final String[] array = getReplacements(event);
        list.addAll(List.of(replacements));
        list.addAll(List.of(array));
        final String[] finalArray = list.toArray(new String[replacements.length+array.length]);
        return trigger(event, getEntities(event), attributes, finalArray);
    }
    default boolean trigger(Event event, HashMap<String, Entity> entities, List<String> attributes, String...replacements) {
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
            default:
                return event instanceof RPEvent ? ((RPEvent) event).getPlayer() : null;
        }
    }
    default void triggerCustomEnchants(Event event, EquippedCustomEnchants equipped, List<String> globalattributes) {
        triggerCustomEnchants(event, equipped, globalattributes, EQUIPMENT_SLOTS);
    }
    default void triggerCustomEnchants(Event event, EquippedCustomEnchants equipped, List<String> globalattributes, EquipmentSlot...slots) {
        triggerCustomEnchants(event, getEntities(event), equipped, globalattributes, slots);
    }
    default void triggerCustomEnchants(Event event, HashMap<String, Entity> entities, EquippedCustomEnchants equipped, List<String> globalattributes) {
        triggerCustomEnchants(event, entities, equipped, globalattributes, false, EQUIPMENT_SLOTS);
    }
    default void triggerCustomEnchants(Event event, HashMap<String, Entity> entities, EquippedCustomEnchants equipped, List<String> globalattributes, EquipmentSlot...slots) {
        triggerCustomEnchants(event, entities, equipped, globalattributes, false, slots);
    }
    default void triggerCustomEnchants(Event event, HashMap<String, Entity> entities, EquippedCustomEnchants equipped, List<String> globalattributes, boolean getEventItem, EquipmentSlot...slots) {
        final String[] eventReplacements = getReplacements(event);
        try {
            if(trigger(event, entities, globalattributes, eventReplacements)) {
            }
        } catch (Exception error) {
            sendConsoleErrorMessage("EventExecutor", "Generated a global attribute error! &e(" + RP_VERSION + ")");
            sendConsoleMessage("&cEquipped Custom Enchants=" + equipped.getEnchants().toString());
            sendConsoleMessage("&cEntities=" + entities.toString());
            sendConsoleMessage("&cReplacements=" + Arrays.toString(eventReplacements));
            sendConsoleMessage("&cGlobal Attributes=" + globalattributes.toString());
            error.printStackTrace();
        }

        final Player player = equipped.getPlayer();
        final String world = player.getWorld().getName();
        final ItemStack eventItem = getEventItem ? equipped.getEventItem() : null;
        for(EquipmentSlot slot : slots) {
            final ItemStack is = getEventItem ? eventItem : equipped.getItem(slot);
            final LinkedHashMap<CustomEnchantSpigot, Integer> enchants = equipped.getEnchantsOn(slot);

            if(is != null && enchants != null) {
                for(CustomEnchantSpigot enchant : enchants.keySet()) {
                    final boolean onCorrectItem = enchant.isOnCorrectItem(is);
                    //final boolean canBeTriggered = enchant.canBeTriggered(event, player, is);
                    //Bukkit.broadcastMessage("EventExecutor;enchant=" + enchant.getIdentifier() + ";event=" + event.getEventName() + ";onCorrectItem=" + onCorrectItem);
                    if(onCorrectItem && enchant.canProcInWorld(world)) {
                        final int lvl = enchants.get(enchant);
                        final String[] enchantReplacements = new String[] {"level", Integer.toString(lvl), "{ENCHANT}", enchant.getName() + " " + toRoman(lvl)}, replacements = getReplacements(eventReplacements, enchantReplacements);
                        final List<String> attributes = enchant.getAttributes();
                        try {
                            if(trigger(event, entities, replaceCE(lvl, attributes), replacements)) {
                                final CustomEnchantProcEvent proc = new CustomEnchantProcEvent(player, event, entities, enchant, lvl, is);
                                PLUGIN_MANAGER.callEvent(proc);
                            }
                        } catch (Exception error) {
                            sendConsoleErrorMessage("EventExecutor", "Custom Enchant with identifier &f" + enchant.getIdentifier() + " &egenerated an attribute error! &e(" + RP_VERSION + ")");
                            sendConsoleErrorMessage("EventExecutor", "Entities=&f" + entities.toString());
                            sendConsoleErrorMessage("EventExecutor", "Replacements=&f" + Arrays.toString(replacements));
                            sendConsoleErrorMessage("EventExecutor", "Attributes=&f" + attributes);
                            error.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
