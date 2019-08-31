package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.addons.PlayerQuest;
import me.randomhashtags.randompackage.addons.RarityGem;
import me.randomhashtags.randompackage.addons.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.addons.objects.CustomEnchantEntity;
import me.randomhashtags.randompackage.addons.objects.ExecutedEventAttributes;
import me.randomhashtags.randompackage.api.PlayerQuests;
import me.randomhashtags.randompackage.events.PlayerQuestCompleteEvent;
import me.randomhashtags.randompackage.events.eventAttributes.ExecuteAttributesEvent;
import me.randomhashtags.randompackage.api.nearFinished.FactionUpgrades;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public abstract class newEventAttributes extends RPFeature {
    private static int loadedInstances = 0;
    private static boolean isenabled = false;

    protected void loadUtils() {
        if(!isenabled) {
            isenabled = true;
        }
        loadedInstances++;
    }
    protected void unloadUtils() {
        if(loadedInstances > 0) loadedInstances--;
        if(isenabled && loadedInstances == 0) {
            isenabled = false;
        }
    }

    public boolean success(Event event, List<String> attributes, HashMap<String, String> attributeReplacements) {
        final boolean success = event != null && attributes != null && !attributes.isEmpty();
        if(success) {
            final ExecuteAttributesEvent e = new ExecuteAttributesEvent(event, attributes, attributeReplacements);
            pluginmanager.callEvent(e);
        }
        return success;
    }
    public List<String> replace(List<String> attributes, HashMap<String, String> attributeReplacements) {
        final List<String> a = new ArrayList<>();
        final boolean not = attributeReplacements != null && !attributeReplacements.isEmpty();
        for(String s : attributes) {
            if(not) {
                for(String r : attributeReplacements.keySet()) {
                    s = s.replace(r, attributeReplacements.get(r));
                }
            }
            a.add(s);
        }
        return a;
    }

    public boolean passedIfs(Event event, TreeMap<String, Entity> entities, String attribute) {
        if(entities.isEmpty()) return true;
        attribute = attribute.toLowerCase();
        final List<Boolean> booleans = new ArrayList<>();
        final boolean cancellable = event instanceof Cancellable, isCancelled = cancellable && ((Cancellable) event).isCancelled();
        for(String s : attribute.split(";")) {
            final String l = s.toLowerCase();
            if(l.startsWith("cancelled=") && cancellable) {
                booleans.add(isCancelled == Boolean.parseBoolean(l.split("=")[1]));
            } else if(l.startsWith("chance=")) {
                booleans.add(getRemainingInt(l.split("=")[1]) >= random.nextInt(100));
            }
            for(String entity : entities.keySet()) {
                final String el = entity.toLowerCase();
                final Entity e = entities.get(entity);
                final LivingEntity le = e instanceof LivingEntity ? (LivingEntity) e : null;
                final boolean isLiving = le != null;
                if(l.startsWith(el + "isholding=")) {
                    final String inhand = UMaterial.match(le.getEquipment().getItemInHand()).name();
                    booleans.add(inhand.endsWith(l.toUpperCase()));
                } else if(l.startsWith(el + "isblocking=")) {
                    booleans.add(le instanceof Player && ((Player) le).isBlocking() == Boolean.parseBoolean(l.split("=")[1]));
                } else if(isLiving && l.startsWith(el + "isswimming=")) {
                    booleans.add(!isLegacy && le.isSwimming() == Boolean.parseBoolean(l.split("=")[1]));
                } else if(l.startsWith(el + "istype=")) {
                    booleans.add(e.getType().name().equalsIgnoreCase(l.split("=")[1]));
                } else if(l.startsWith(el + "issneaking=")) {
                    booleans.add(e instanceof Player && ((Player) e).isSneaking() == Boolean.parseBoolean(l.split("=")[1]));
                } else if(l.startsWith(el + "isfacing=")) {
                    booleans.add(e.getFacing().name().startsWith(l.split("=")[1].toUpperCase()));
                } else if(l.equals(el + "isplayer")) {
                    booleans.add(e instanceof Player);
                } else if(l.equals(el + "!isplayer")) {
                    booleans.add(!(e instanceof Player));
                } else if(l.equals(el + "ismob")) {
                    booleans.add(e instanceof Mob);
                } else if(l.equals(el + "iscreature")) {
                    booleans.add(e instanceof Creature);
                }
            }
        }
        return !booleans.contains(false);
    }
    public ExecutedEventAttributes doGenericAttribute(Event event, TreeMap<String, Entity> entities, String attribute, String wholeAttribute) {
        final String attributeLowercase = attribute.toLowerCase();
        if(passedIfs(event, entities, attribute)) {
            final LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
            if(attributeLowercase.startsWith("dropitem=")) {
            } else if(attributeLowercase.startsWith("playsound=")) {
                final String[] a = attributeLowercase.split("=")[1].split(":");
                final Location l = toLocation(a[0]);
                final String sound = a[1];
                final int pitch = Integer.parseInt(a[2]), volume = Integer.parseInt(a[3]), playtimes = Integer.parseInt(a[4]);
                playSound(sound, pitch, volume, l, playtimes, attributeLowercase.endsWith(":true"));
            } else if(attributeLowercase.startsWith("spawnentity=")) {
                final String[] s = attributeLowercase.split("=")[1].split(":");
                final CustomEnchantEntity e = CustomEnchantEntity.paths.get(s[0]);
                if(e != null) {
                    final Entity summoner = entities.get(s[1]), target = entities.get(s[2]);
                    if(summoner instanceof LivingEntity && target instanceof LivingEntity) {
                        e.spawn((LivingEntity) summoner, (LivingEntity) target, null);
                    }
                }
            } else if(attributeLowercase.startsWith("smite=")) {
                final String[] a = attribute.split("=")[1].split(":");
                final Location l = toLocation(a[0]);
                if(l != null) {
                    final World w = l.getWorld();
                    final int amount = (int) evaluate(a[1]);
                    for(int i = 1; i <= amount; i++) {
                        w.strikeLightning(l);
                    }
                }
            } else if(attributeLowercase.startsWith("executecommand=")) {
                Bukkit.dispatchCommand(console, attribute.split("=")[1]);
            } else {
                for(String entity : entities.keySet()) {
                    boolean did = true;
                    final String el = entity.toLowerCase();
                    final Entity e = entities.get(entity);
                    final LivingEntity le = e instanceof LivingEntity ? (LivingEntity) e : null;
                    final boolean isLiving = le != null;
                    if(attributeLowercase.startsWith("ignite" + el + "=")) {
                        e.setFireTicks((int) evaluate(attributeLowercase.split("=")[1]));
                    } else if(isLiving) {
                        if(attributeLowercase.startsWith("damage" + el + "=")) {
                            final double hp = le.getHealth();
                            double dmg = evaluate(attributeLowercase.split("=")[1]);
                            dmg = hp < dmg ? hp : dmg;
                            le.damage(dmg);
                        } else if(attributeLowercase.startsWith("set" + el + "hunger=")) {
                            if(le instanceof Player) {
                                final Player p = (Player) le;
                                final int lvl = p.getFoodLevel(), n = (int) evaluate(attributeLowercase.split("=")[1].replace("lvl", Integer.toString(lvl)));
                                p.setFoodLevel(n > 20 ? 20 : n);
                            }
                        } else if(attributeLowercase.startsWith("set" + el + "air=")) {
                            final int r = le.getRemainingAir(), m = le.getMaximumAir(), value = (int) evaluate(attributeLowercase.split("=")[1].replace("air", Integer.toString(r)));
                            le.setRemainingAir(value > m ? m : value);
                        } else if(attributeLowercase.startsWith("set" + el + "health=")) {
                            final double newHealth = evaluate(attributeLowercase.split("=")[1].replace("hp", Double.toString(le.getHealth())));
                            le.setHealth(newHealth < 0 ? 0.00 : newHealth);
                        } else if(attributeLowercase.startsWith("addpotioneffectto" + el + "=")) {
                            final String[] potion = attributeLowercase.split("=")[1].split(":");
                            addPotionEffect(le, getPotionEffectType(potion[0]), (int) evaluate(potion[1]), (int) evaluate(potion[2]), true, true, true);
                        } else if(attributeLowercase.startsWith("addpotioneffectsto" + el + "=")) {
                            for(String s : attributeLowercase.split("=")[1].split("\\|")) {
                                final String[] potion = s.split(":");
                                addPotionEffect(le, getPotionEffectType(potion[0]), (int) evaluate(potion[1]), (int) evaluate(potion[2]), true, true, true);
                            }
                        } else if(attributeLowercase.startsWith("removepotioneffectfrom" + el + "=")) {
                            removePotionEffect(le, getPotionEffectType(attributeLowercase.split("=")[1]));
                        } else if(attributeLowercase.startsWith("removepotioneffectsfrom" + el + "=")) {
                            for(String s : attributeLowercase.split("=")[1].split("\\|")) {
                                removePotionEffect(le, getPotionEffectType(s));
                            }
                        } else if(attributeLowercase.startsWith("increase" + el + "playerquest=")) {
                            final PlayerQuests q = PlayerQuests.getPlayerQuests();
                            if(q.isEnabled()) {
                                final List<String> completed = q.config.getStringList("messages.completed");
                                if(e instanceof Player) {
                                    final Player player = (Player) e;
                                    final Collection<ActivePlayerQuest> a = RPPlayer.get(player.getUniqueId()).getQuests().values();
                                    final double value = Double.parseDouble(attribute.split("=")[1]);
                                    for(ActivePlayerQuest quest : a) {
                                        tryIncreasingPlayerQuest(event, entities, player, quest, value, completed);
                                    }
                                }
                            }
                        } else if(attributeLowercase.startsWith("deplete" + el + "raritygem=")) {
                            if(e instanceof Player) {
                                final Player player = (Player) e;
                                final UUID u = player.getUniqueId();
                                final RPPlayer pdata = RPPlayer.get(u);
                                final String[] a = attribute.split("=")[1].split(":");
                                final RarityGem gem = getRarityGem(a[0]);
                                if(gem != null && pdata.hasActiveRarityGem(gem)) {
                                    final ItemStack g = getRarityGem(gem, player);
                                    if(g != null) {
                                        itemMeta = g.getItemMeta();
                                        final int amount = getRemainingInt(itemMeta.getDisplayName());
                                        int depleteAmount = Integer.parseInt(a[1]);

                                        final FactionUpgrades fu = FactionUpgrades.getFactionUpgrades();
                                        if(fu.isEnabled() && hookedFactionsUUID()) {
                                            depleteAmount -= depleteAmount*fu.getDecreaseRarityGemPercent(factions.getRegionalIdentifier(u), gem);
                                        }

                                        if(amount - depleteAmount <= 0) {
                                            depleteAmount = amount;
                                            pdata.toggleRarityGem(null, gem);
                                        }
                                        itemMeta = g.getItemMeta();
                                        itemMeta.setDisplayName(gem.getItem().getItemMeta().getDisplayName().replace("{SOULS}", Integer.toString(amount - depleteAmount)));
                                        g.setItemMeta(itemMeta);
                                    }
                                }
                            }
                        } else if(attributeLowercase.startsWith("give" + el + "exp=") || attributeLowercase.startsWith("set" + el + "exp=")) {
                            if(e instanceof Player) {
                                final boolean set = attributeLowercase.startsWith("set");
                                final Player p = (Player) e;
                                final int exp = getTotalExperience(p), xp = (int) evaluate(attributeLowercase.split("=")[1].replace("xp", Integer.toString(exp)));
                                if(set) setTotalExperience(p, xp);
                                else p.giveExp(xp);
                            }
                        }
                    } else if(attributeLowercase.startsWith("send" + el + "message=")) {
                        e.sendMessage(ChatColor.translateAlternateColorCodes('&', attribute.split(attribute.split("=")[0].length() + "=")[1]));
                    } else {
                        did = false;
                    }
                    if(did) {
                        attributes.put(wholeAttribute, attributeLowercase);
                    }
                }
            }
            return attributes.isEmpty() ? null : new ExecutedEventAttributes(event, attributes);
        }
        return null;
    }

    private void addPotionEffect(LivingEntity entity, PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, boolean icon) {
        if(entity != null && type != null) {
            if(isLegacy) {
                entity.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles));
            } else {
                entity.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles, icon));
            }
        }
    }
    private void removePotionEffect(LivingEntity entity, PotionEffectType type) {
        if(entity != null && type != null) {
            entity.removePotionEffect(type);
        }
    }


    public void tryIncreasingPlayerQuest(Event event, TreeMap<String, Entity> entities, Player player, ActivePlayerQuest activeQuest, double value, List<String> completedMsg) {
        tryIncreasingPlayerQuest(event, entities, player, activeQuest, value, completedMsg, false);
    }
    public void tryIncreasingPlayerQuest(Event event, TreeMap<String, Entity> entities, Player player, ActivePlayerQuest activeQuest, double value, List<String> completedMsg, boolean checkIfs) {
        if(event != null && entities != null && player != null && activeQuest != null && !activeQuest.isCompleted()) {
            final PlayerQuest quest = activeQuest.getQuest();
            if(checkIfs) {
                final String trigger = event.getEventName().replace("Event", "");
                for(String s : quest.getTrigger()) {
                    final String[] A = s.split(";");
                    if(trigger.equals(A[0])) {
                        for(String attribute : s.split(A[0] + ";")[1].split(";")) {
                            if(passedIfs(event, entities, attribute)) {
                                increasePlayerQuest(player, activeQuest, quest, value, completedMsg);
                            }
                        }
                    }
                }
            } else {
                increasePlayerQuest(player, activeQuest, quest, value, completedMsg);
            }
        }
    }
    private void increasePlayerQuest(Player player, ActivePlayerQuest activeQuest, PlayerQuest quest, double value, List<String> completed) {
        activeQuest.setProgress(activeQuest.getProgress()+value);
        final double timer = quest.getTimedCompletion();
        if(timer > 0.00) {
        } else if(activeQuest.getProgress() >= Double.parseDouble(quest.getCompletion())) {
            activeQuest.setCompleted(true);
            final PlayerQuestCompleteEvent e = new PlayerQuestCompleteEvent(player, activeQuest);
            pluginmanager.callEvent(e);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{NAME}", quest.getName());
            sendStringListMessage(player, completed, replacements);
        }
    }
    public TreeMap<String, Entity> getEntities(Object...values) {
        final TreeMap<String, Entity> e = new TreeMap<>();
        for(int i = 0; i < values.length; i++) {
            if(i%2 == 1) {
                e.put((String) values[i-1], (LivingEntity) values[i]);
            }
        }
        return e;
    }

    private ExecutedEventAttributes doAttribute(EntityDeathEvent event, LivingEntity victim, Player killer, String attribute, String wholeAttribute) {
        String attributeLowercase = attribute.toLowerCase();
        final int xp = event.getDroppedExp();
        if(attributeLowercase.startsWith("droppedxp=")) {
            final String s = attributeLowercase.split("=")[1].replace("xp", Integer.toString(xp));
            event.setDroppedExp((int) evaluate(s));
            final LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
            attributes.put(wholeAttribute, attributeLowercase);
            return new ExecutedEventAttributes(event, attributes);
        } else {
            if(attributeLowercase.contains("victimlocation")) attributeLowercase = attributeLowercase.replace("victimlocation", toString(victim.getLocation()));
            if(killer != null && attributeLowercase.contains("killerlocation")) attributeLowercase = attributeLowercase.replace("killerlocation", toString(killer.getLocation()));
            return doGenericAttribute(event, getEntities("Victim", victim, "Killer", killer), attribute, attributeLowercase);
        }
    }
    public List<ExecutedEventAttributes> executeAttributes(EntityDeathEvent event, List<String> attributes, HashMap<String, String> attributeReplacements) {
        if(success(event, attributes, attributeReplacements)) {
            attributes = replace(attributes, attributeReplacements);
            final LivingEntity victim = event.getEntity();
            final Player killer = victim.getKiller();
            final List<ExecutedEventAttributes> e = new ArrayList<>();
            for(String s : attributes) {
                if(s.toLowerCase().startsWith("entitydeath;")) {
                    for(String a : s.split(s.split(";")[0] + ";")[1].split(";")) {
                        e.add(doAttribute(event, victim, killer, a, s));
                    }
                }
            }
            if(killer != null) killer.updateInventory();
            if(!e.isEmpty()) return e;
        }
        return null;
    }
}
