package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.addons.RarityGem;
import me.randomhashtags.randompackage.addons.objects.CustomEnchantEntity;
import me.randomhashtags.randompackage.api.events.eventAttributes.ExecuteAttributesEvent;
import me.randomhashtags.randompackage.api.nearFinished.FactionUpgrades;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public abstract class newEventAttributes extends RPFeature {
    private static boolean isenabled = false;
    private final boolean isLegacy = version.contains("1.8") || version.contains("1.9") || version.contains("1.10") || version.contains("1.11") || version.contains("1.12");


    private void loadUtils() {
        if(!isenabled) {
            isenabled = true;
        }
    }
    private void unloadUtils() {
        if(isenabled) {
            isenabled = false;
        }
    }

    private boolean success(Event event, List<String> attributes, HashMap<String, String> attributeReplacements) {
        final boolean success = event != null && attributes != null && !attributes.isEmpty();
        if(success) {
            final ExecuteAttributesEvent e = new ExecuteAttributesEvent(event, attributes, attributeReplacements);
            pluginmanager.callEvent(e);
        }
        return success;
    }
    private List<String> replace(List<String> attributes, HashMap<String, String> attributeReplacements) {
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

    private boolean passedIfs(Event event, TreeMap<String, LivingEntity> entities, String attribute) {
        if(entities.isEmpty()) return true;
        attribute = attribute.toLowerCase();
        final List<Boolean> booleans = new ArrayList<>();
        final boolean cancellable = event instanceof Cancellable;
        for(String s : attribute.split(";")) {
            final String l = s.toLowerCase();
            if(l.startsWith("cancelled=") && cancellable) {
                booleans.add(((Cancellable) event).isCancelled() && Boolean.parseBoolean(l.split("=")[1]));
            }
            for(String entity : entities.keySet()) {
                final String el = entity.toLowerCase();
                final LivingEntity le = entities.get(entity);
                if(l.startsWith(el + "isholding=")) {
                    final String inhand = UMaterial.match(le.getEquipment().getItemInHand()).name();
                    booleans.add(inhand.endsWith(l.toUpperCase()));
                } else if(l.startsWith(el + "isblocking=")) {
                    booleans.add(le instanceof Player && ((Player) le).isBlocking() == Boolean.parseBoolean(l.split("=")[1]));
                } else if(l.startsWith(el + "isswimming=")) {
                    booleans.add(!isLegacy && le.isSwimming() == Boolean.parseBoolean(l.split("=")[1]));
                } else if(l.startsWith(el + "istype=")) {
                    booleans.add(le.getType().name().equalsIgnoreCase(l.split("=")[1]));
                } else if(l.startsWith(el + "issneaking=")) {
                    booleans.add(le instanceof Player && ((Player) le).isSneaking() == Boolean.parseBoolean(l.split("=")[1]));
                } else if(l.startsWith(el + "isfacing=")) {
                    booleans.add(le.getFacing().name().startsWith(l.split("=")[1].toUpperCase()));
                }
            }
        }
        return !booleans.contains(false);
    }
    private void doGenericAttribute(Event event, TreeMap<String, LivingEntity> entities, String attribute, String attributeLowercase) {
        if(passedIfs(event, entities, attribute)) {
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
                    final LivingEntity summoner = entities.get(s[1]), target = entities.get(s[2]);
                    e.spawn(summoner, target, null);
                }
            } else if(attributeLowercase.startsWith("smite=")) {
                final String[] a = attribute.split("=")[1].split(":");
                final Location l = toLocation(a[0]);
                if(l != null) {
                    final World w = l.getWorld();
                    final int amount = (int) eval(a[1]);
                    for(int i = 1; i <= amount; i++) {
                        w.strikeLightning(l);
                    }
                }
            } else {
                for(String entity : entities.keySet()) {
                    final String el = entity.toLowerCase();
                    final LivingEntity le = entities.get(entity);
                    if(attributeLowercase.startsWith("give" + el + "exp=") || attributeLowercase.startsWith("set" + el + "exp=")) {
                        if(le instanceof Player) {
                            final boolean set = attributeLowercase.startsWith("set");
                            final Player p = (Player) le;
                            final int exp = getTotalExperience(p), xp = (int) eval(attributeLowercase.split("=")[1].replace("xp", Integer.toString(exp)));
                            if(set) setTotalExperience(p, xp);
                            else p.giveExp(xp);
                        }
                    } else if(attributeLowercase.startsWith("deplete" + el + "raritygem=")) {
                        if(le instanceof Player) {
                            final Player player = (Player) le;
                            final RPPlayer pdata = RPPlayer.get(le.getUniqueId());
                            final String[] a = attribute.split("=")[1].split(":");
                            final RarityGem gem = getRarityGem(a[0]);
                            if(gem != null && pdata.hasActiveRarityGem(gem)) {
                                final ItemStack g = getRarityGem(gem, player);
                                if(g != null) {
                                    itemMeta = g.getItemMeta();
                                    final int amount = getRemainingInt(itemMeta.getDisplayName());
                                    int depleteAmount = Integer.parseInt(a[1]);

                                    final FactionUpgrades fu = FactionUpgrades.getFactionUpgrades();
                                    if(fu.isEnabled()) {
                                        depleteAmount -= depleteAmount*fu.getDecreaseRarityGemPercent(fapi.getFaction(player), gem);
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
                    } else if(attributeLowercase.startsWith("set" + el + "air=")) {
                        final int r = le.getRemainingAir(), m = le.getMaximumAir(), value = (int) eval(attributeLowercase.split("=")[1].replace("air", Integer.toString(r)));
                        le.setRemainingAir(value > m ? m : value);
                    } else if(attributeLowercase.startsWith("set" + el + "health=")) {
                        final double newHealth = eval(attributeLowercase.split("=")[1].replace("hp", Double.toString(le.getHealth())));
                        le.setHealth(newHealth < 0 ? 0.00 : newHealth);
                    } else if(attributeLowercase.startsWith("set" + el + "hunger=")) {
                        if(le instanceof Player) {
                            final Player p = (Player) le;
                            final int lvl = p.getFoodLevel(), n = (int) eval(attributeLowercase.split("=")[1].replace("lvl", Integer.toString(lvl)));
                            p.setFoodLevel(n > 20 ? 20 : n);
                        }
                    } else if(attributeLowercase.startsWith("damage" + el + "=")) {
                        final double hp = le.getHealth();
                        double dmg = eval(attributeLowercase.split("=")[1]);
                        dmg = hp < dmg ? hp : dmg;
                        le.damage(dmg);
                    } else if(attributeLowercase.startsWith("addpotioneffectto" + el + "=")) {
                        final String[] potion = attributeLowercase.split("=")[1].split(":");
                        final PotionEffectType type = getPotionEffectType(potion[0]);
                        if(type != null) {
                            final int duration = (int) eval(potion[1]), amplifier = (int) eval(potion[2]);
                            final boolean ambient = true, particles = true, icon = true;
                            if(isLegacy) {
                                le.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles));
                            } else {
                                le.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles, icon));
                            }
                        }
                    } else if(attributeLowercase.startsWith("ignite" + el + "=")) {
                        le.setFireTicks((int) eval(attributeLowercase.split("=")[1]));
                    }
                }
            }
        }
    }
    private TreeMap<String, LivingEntity> getEntities(Object...values) {
        final TreeMap<String, LivingEntity> e = new TreeMap<>();
        for(int i = 0; i < values.length; i++) {
            if(i%2 == 1) {
                e.put((String) values[i-1], (LivingEntity) values[i]);
            }
        }
        return e;
    }
    private ItemStack getRarityGem(RarityGem gem, Player player) {
        final PlayerInventory pi = player.getInventory();
        final List<String> l = gem.getItem().getItemMeta().getLore();
        for(int i = 0; i < pi.getSize(); i++) {
            final ItemStack a = pi.getItem(i);
            if(a != null && a.hasItemMeta() && a.getItemMeta().hasLore() && a.getItemMeta().getLore().equals(l)) {
                return a;
            }
        }
        return null;
    }


    private void doAttribute(PlayerInteractEvent event, Player player, String attribute, String attributeLowercase) {
        if(attributeLowercase.equals("cancel")) {
            event.setCancelled(true);
        } else {
            if(attributeLowercase.contains("playerlocation")) attributeLowercase = attribute.replace("playerlocation", toString(player.getLocation()));
            doGenericAttribute(event, getEntities("Player", player), attribute, attributeLowercase);
        }
    }
    private void doAttribute(EntityDeathEvent event, LivingEntity victim, Player killer, String attribute, String attributeLowercase) {
        final int xp = event.getDroppedExp();
        if(attributeLowercase.startsWith("droppedxp=")) {
            event.setDroppedExp((int) eval(attributeLowercase.split("=")[1].replace("droppedxp", Integer.toString(xp))));
        } else {
            if(attributeLowercase.contains("victimlocation")) attributeLowercase = attribute.replace("victimlocation", toString(victim.getLocation()));
            if(attributeLowercase.contains("killerlocation")) attributeLowercase = attribute.replace("killerlocation", toString(killer.getLocation()));
            doGenericAttribute(event, getEntities("Victim", victim, "Killer", killer), attribute, attributeLowercase);
        }
    }

    public void executeAttributes(PlayerInteractEvent event, List<String> attributes, HashMap<String, String> attributeReplacements) {
        if(success(event, attributes, attributeReplacements)) {
            attributes = replace(attributes, attributeReplacements);
            final Player player = event.getPlayer();
            for(String s : attributes) {
                if(s.startsWith("PlayerInteract;")) {
                    for(String a : s.split(s.split(";")[0] + ";")[1].split(";")) {
                        doAttribute(event, player, a, a.toLowerCase());
                    }
                }
            }
            player.updateInventory();
        }
    }
    public void executeAttributes(EntityDeathEvent event, List<String> attributes, HashMap<String, String> attributeReplacements) {
        if(success(event, attributes, attributeReplacements)) {
            attributes = replace(attributes, attributeReplacements);
            final LivingEntity victim = event.getEntity();
            final Player killer = victim.getKiller();
            if(killer != null) {
                final boolean victimIsPlayer = victim instanceof Player;
                for(String s : attributes) {
                    if(s.startsWith("KilledEntity;") && !victimIsPlayer || s.startsWith("KilledPlayer;") && victimIsPlayer) {
                        for(String a : s.split(s.split(";")[0] + ";")[1].split(";")) {
                            doAttribute(event, victim, killer, a, a.toLowerCase());
                        }
                    }
                }
                killer.updateInventory();
            }
        }
    }
}
